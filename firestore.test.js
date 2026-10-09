const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_admin_123";
const BOB_UID = "bob_user_456";

const [emulatorHost, emulatorPortStr] = (process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085").split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read gallery_photos or admissions", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("gallery_photos").get());
  await assertFails(unauthDb.collection("admissions").get());
});

test("Authenticated user: can create and query own gallery photo with occasionTheme", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("gallery_photos").doc("photo_1").set({
      id: "photo_1",
      userId: ALICE_UID,
      occasionTheme: "Saraswati Puja & Basant Utsav",
      title: "Campus Saraswati Vandana Ceremony",
      imageSource: "PRESET_CAMPUS",
      dateLabel: "08 Oct 2026",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("gallery_photos").where("userId", "==", ALICE_UID).get()
  );
});

test("Authenticated user: fails query without userId filter (rules are not filters)", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(aliceDb.collection("gallery_photos").get());
});

test("Cross-user isolation: Bob cannot read or delete Alice's gallery photo", async () => {
  const now = new Date();
  await testEnv.withSecurityRulesDisabled(async (ctx) => {
    await ctx.firestore().collection("gallery_photos").doc("alice_p1").set({
      id: "alice_p1",
      userId: ALICE_UID,
      occasionTheme: "Annual Sports Meet",
      title: "Track & Field Finals",
      imageSource: "PRESET_SPORTS",
      dateLabel: "08 Oct 2026",
      createdAt: now,
      updatedAt: now,
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  await assertFails(bobDb.collection("gallery_photos").doc("alice_p1").get());
  await assertFails(bobDb.collection("gallery_photos").doc("alice_p1").delete());
});

test("Admission status update: owner/admin can update status to ACCEPTED or CANCELLED, rejects invalid status", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertSucceeds(
    aliceDb.collection("admissions").doc("adm_101").set({
      id: "adm_101",
      userId: ALICE_UID,
      studentName: "Ankita Kumari",
      fatherName: "Rajesh Kumar Rai",
      classApplied: "Class 11",
      streamApplied: "Science (I.Sc.)",
      prevPercentage: "89.4%",
      mobileNumber: "9431084512",
      village: "Gangauli",
      status: "PENDING",
      createdAt: now,
      updatedAt: now,
    })
  );

  await assertSucceeds(
    aliceDb.collection("admissions").doc("adm_101").update({
      status: "ACCEPTED",
      updatedAt: new Date(),
    })
  );

  // Invalid status enum must fail
  await assertFails(
    aliceDb.collection("admissions").doc("adm_101").update({
      status: "INVALID_STATUS",
      updatedAt: new Date(),
    })
  );
});

test("Shadow update test: rejects ghost field injection on teacher registration", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const now = new Date();
  await assertFails(
    aliceDb.collection("teachers").doc("t_1").set({
      id: "t_1",
      userId: ALICE_UID,
      name: "Dr. Rajeev Mishra",
      designation: "PGT Physics",
      department: "Science",
      subject: "Physics",
      qualification: "Ph.D., B.Ed.",
      experience: "14 Years",
      phone: "9431000111",
      ghostField: true,
      createdAt: now,
      updatedAt: now,
    })
  );
});
