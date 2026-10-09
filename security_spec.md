# Security Specification — UMV Gangauli Portal (`security_spec.md`)

## 1. Data Invariants
1. **Authentication Mandatory**: Every read (`get`, `list`) and write (`create`, `update`, `delete`) across `/admins`, `/gallery_photos`, `/admissions`, `/school_config`, `/live_results`, and `/teachers` requires `request.auth != null`.
2. **Ownership & Admin Isolation**: Every document stores `userId` matching `request.auth.uid`. Reads (`get`, `list`) and mutations are restricted to `resource.data.userId == request.auth.uid || isAdmin()`.
3. **Strict Key Allowlisting & Validation Blueprints**: Every `create` and `update` invokes `isValid[Entity](incoming())`, validates string length limits, enforces `createdAt <= request.time` and `updatedAt <= request.time`, and locks immutable fields (`id`, `userId`, `createdAt`) on update.
4. **Path ID Hardening**: All single-document operations (`get`, `create`, `update`, `delete`) validate `isValidId(docId)` (`^[a-zA-Z0-9_\-]+$`, max 128 chars).

## 2. The "Dirty Dozen" Adversarial Payloads
1. **Unauthenticated Read**: `unauthDb.collection("gallery_photos").get()` -> Reject (`PERMISSION_DENIED`).
2. **Cross-User Read**: Alice reads Bob's admission record `/admissions/bob_adm` -> Reject (`PERMISSION_DENIED`).
3. **Unfiltered List Query**: Authenticated user calls `db.collection("gallery_photos").get()` without `.where("userId", "==", uid)` -> Reject (`PERMISSION_DENIED`).
4. **Identity Spoofing on Create**: Alice creates `/gallery_photos/p1` with `userId: "bob_456"` -> Reject (`PERMISSION_DENIED`).
5. **Shadow / Ghost Field Injection**: Client sends extra field `isSuperAdmin: true` in `/teachers/t1` -> Reject via `.keys().hasOnly(...)`.
6. **Immutable Field Mutation**: Client updates `createdAt` or `userId` on `/admissions/adm1` -> Reject (`incoming().userId == existing().userId`).
7. **Invalid Admission Status Enum**: Client updates `/admissions/adm1` with `status: "HACKED"` (not in `['PENDING', 'ACCEPTED', 'CANCELLED']`) -> Reject.
8. **Future Timestamp Spoofing**: Client sets `createdAt` in the future (`> request.time`) -> Reject.
9. **String Size Overflow (Denial of Wallet)**: Client sends 5,000-char string for `title` in `/gallery_photos/p1` -> Reject (`size() <= 200`).
10. **Invalid Numeric Type in Results**: Client sets `totalMarks: "450"` (string instead of int) or `totalMarks: 9999` (`> 500`) in `/live_results/r1` -> Reject.
11. **Unverified Admin Email Spoofing**: Token has `email: "foundationjjf@gmail.com"` with `email_verified: false` -> Reject.
12. **Unauthorized Delete**: Alice attempts to delete Bob's gallery photo `/gallery_photos/bob_photo` -> Reject.
