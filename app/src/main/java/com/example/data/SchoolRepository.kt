package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.model.*
import org.json.JSONArray
import org.json.JSONObject

class SchoolRepository(context: Context? = null) {

    private val prefs: SharedPreferences? =
        context?.getSharedPreferences("umv_gangauli_portal_prefs", Context.MODE_PRIVATE)

    fun getLanguagePreference(): Boolean = prefs?.getBoolean("is_hindi", true) ?: true
    fun setLanguagePreference(isHindi: Boolean) {
        prefs?.edit()?.putBoolean("is_hindi", isHindi)?.apply()
    }

    fun getDarkThemePreference(): Boolean = prefs?.getBoolean("is_dark_theme", false) ?: false
    fun setDarkThemePreference(isDark: Boolean) {
        prefs?.edit()?.putBoolean("is_dark_theme", isDark)?.apply()
    }

    fun getCompletedHomeworkIds(): Set<String> =
        prefs?.getStringSet("completed_hw_ids", setOf("hw_1")) ?: setOf("hw_1")

    fun saveCompletedHomeworkIds(ids: Set<String>) {
        prefs?.edit()?.putStringSet("completed_hw_ids", ids)?.apply()
    }

    fun getBookmarkedNoticeIds(): Set<String> =
        prefs?.getStringSet("bookmarked_notice_ids", setOf("notice_1")) ?: setOf("notice_1")

    fun saveBookmarkedNoticeIds(ids: Set<String>) {
        prefs?.edit()?.putStringSet("bookmarked_notice_ids", ids)?.apply()
    }

    fun getCheckedDocIds(): Set<String> =
        prefs?.getStringSet("checked_doc_ids", setOf("doc_1", "doc_2")) ?: setOf("doc_1", "doc_2")

    fun saveCheckedDocIds(ids: Set<String>) {
        prefs?.edit()?.putStringSet("checked_doc_ids", ids)?.apply()
    }

    fun getSavedAdmissions(): List<AdmissionApplication> {
        val raw = prefs?.getString("saved_admissions_json", null)
        if (raw.isNullOrBlank()) {
            return listOf(
                AdmissionApplication(
                    applicationId = "UMVG-2026-1042",
                    studentName = "अंकिता कुमारी (Ankita Kumari)",
                    fatherName = "श्री राजेश कुमार राय (Rajesh Kumar Rai)",
                    motherName = "श्रीमती सुनीता देवी (Sunita Devi)",
                    classApplied = "Class 11 (+2 Intermediate)",
                    streamApplied = "Science (I.Sc. - PCM)",
                    prevPercentage = "89.4%",
                    mobileNumber = "9431084512",
                    villagePanchayat = "Gangauli (गंगौली, सिमरी)",
                    category = "General / EWS",
                    aadhaarLast4 = "4829",
                    submittedDate = "08 Oct 2026",
                    status = "Verified • काउंसलिंग हेतु चयनित"
                )
            )
        }
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<AdmissionApplication>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    AdmissionApplication(
                        applicationId = obj.optString("applicationId"),
                        studentName = obj.optString("studentName"),
                        fatherName = obj.optString("fatherName"),
                        motherName = obj.optString("motherName"),
                        classApplied = obj.optString("classApplied"),
                        streamApplied = obj.optString("streamApplied"),
                        prevPercentage = obj.optString("prevPercentage"),
                        mobileNumber = obj.optString("mobileNumber"),
                        villagePanchayat = obj.optString("villagePanchayat"),
                        category = obj.optString("category"),
                        aadhaarLast4 = obj.optString("aadhaarLast4"),
                        submittedDate = obj.optString("submittedDate"),
                        status = obj.optString("status")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveAdmissions(list: List<AdmissionApplication>) {
        val arr = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("applicationId", item.applicationId)
                put("studentName", item.studentName)
                put("fatherName", item.fatherName)
                put("motherName", item.motherName)
                put("classApplied", item.classApplied)
                put("streamApplied", item.streamApplied)
                put("prevPercentage", item.prevPercentage)
                put("mobileNumber", item.mobileNumber)
                put("villagePanchayat", item.villagePanchayat)
                put("category", item.category)
                put("aadhaarLast4", item.aadhaarLast4)
                put("submittedDate", item.submittedDate)
                put("status", item.status)
            }
            arr.put(obj)
        }
        prefs?.edit()?.putString("saved_admissions_json", arr.toString())?.apply()
    }

    fun getSavedRequests(): List<LeaveOrFeedbackRequest> {
        val raw = prefs?.getString("saved_requests_json", null)
        if (raw.isNullOrBlank()) {
            return listOf(
                LeaveOrFeedbackRequest(
                    id = "REQ-901",
                    studentName = "रोहित कुमार पाण्डेय (Rohit Kumar Pandey)",
                    classAndRoll = "Class 12 Science • Roll 260101",
                    requestType = "FEEDBACK",
                    message = "उन्नयन स्मार्ट क्लास में 3D फिजिक्स एनिमेशन से ऑप्टिक्स अध्याय बहुत स्पष्ट समझ में आया। धन्यवाद!",
                    dateSubmitted = "06 Oct 2026",
                    status = "प्राचार्य द्वारा स्वीकृत • Acknowledged"
                )
            )
        }
        return try {
            val arr = JSONArray(raw)
            val list = mutableListOf<LeaveOrFeedbackRequest>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    LeaveOrFeedbackRequest(
                        id = obj.optString("id"),
                        studentName = obj.optString("studentName"),
                        classAndRoll = obj.optString("classAndRoll"),
                        requestType = obj.optString("requestType"),
                        message = obj.optString("message"),
                        dateSubmitted = obj.optString("dateSubmitted"),
                        status = obj.optString("status")
                    )
                )
            }
            list
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun saveRequests(list: List<LeaveOrFeedbackRequest>) {
        val arr = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("studentName", item.studentName)
                put("classAndRoll", item.classAndRoll)
                put("requestType", item.requestType)
                put("message", item.message)
                put("dateSubmitted", item.dateSubmitted)
                put("status", item.status)
            }
            arr.put(obj)
        }
        prefs?.edit()?.putString("saved_requests_json", arr.toString())?.apply()
    }

    val liveTickerNotices = listOf(
        BilingualText(
            "🔴 सत्र 2026-27 हेतु कक्षा 9वीं एवं 11वीं (विज्ञान, कला एवं वाणिज्य) में ऑनलाइन नामांकन प्रारंभ है — अभी आवेदन करें!",
            "🔴 Online Admissions Open for Session 2026-27 in Class 9th & 11th (Science, Arts & Commerce) — Apply Now!"
        ),
        BilingualText(
            "🏆 बक्सर जिला बोर्ड परीक्षा में उच्च माध्यमिक विद्यालय गंगौली का 98.6% उत्तीर्णता के साथ उत्कृष्ट प्रदर्शन!",
            "🏆 UMV Gangauli achieves 98.6% Pass Result with District Top-10 Rankers in BSEB Board Examinations!"
        ),
        BilingualText(
            "💻 उन्नयन बिहार स्मार्ट क्लास एवं ICT कंप्यूटर लैब में प्रतिदिन AI और डिजिटल कोडिंग कार्यशाला का संचालन।",
            "💻 Daily AI & Digital Literacy Workshops running in Unnayan Bihar Smart Class & ICT Computer Lab."
        ),
        BilingualText(
            "🎓 मेधासॉफ्ट छात्रवृत्ति, मुख्यमंत्री कन्या उत्थान (₹25,000) एवं साइकिल/पोशाक योजना का ई-केवाईसी सत्यापन जारी।",
            "🎓 MedhaSoft Scholarship, Mukhyamantri Kanya Utthan (₹25,000) & DBT e-KYC verification open at Campus Helpdesk."
        )
    )

    val campusFacilities = listOf(
        FacilityItem(
            id = "fac_smart_class",
            title = BilingualText(
                "उन्नयन बिहार स्मार्ट क्लास एवं ICT कंप्यूटर लैब",
                "Unnayan Bihar Smart Class & ICT Digital Lab"
            ),
            subtitle = BilingualText(
                "4K इंटरएक्टिव पैनल • 40 हाई-स्पीड कंप्यूटर • ई-लर्निंग स्टूडियो",
                "4K Interactive Flat Panels • 40 High-Speed PCs • e-Learning Studio"
            ),
            description = BilingualText(
                "उच्च माध्यमिक विद्यालय गंगौली में बिहार शिक्षा परियोजना एवं उन्नयन बिहार के अंतर्गत अत्याधुनिक स्मार्ट क्लासरूम स्थापित हैं। यहाँ 3D एनिमेशन, वीडियो लेक्चर और डिजिटल सिमुलेशन के माध्यम से भौतिकी, गणित, जीव विज्ञान और अंग्रेजी की जटिल अवधारणाओं को रोचक बनाया जाता है।",
                "Equipped under the flagship Unnayan Bihar & ICT @ School initiative, our smart classrooms bring 3D simulations, interactive animations, and digital coding directly to students of Gangauli and Simri Block."
            ),
            highlights = listOf(
                BilingualText("75-इंच 4K इंटरएक्टिव स्मार्ट बोर्ड एवं हाई-स्पीड फाइबर इंटरनेट", "75-inch 4K Interactive Smartboard & High-Speed Fiber Internet"),
                BilingualText("40 आधुनिक डेस्कटॉप सिस्टम के साथ पाइथन, HTML एवं टैली प्रशिक्षण", "40 Modern Desktops with Python, Web Design & Office Productivity Training"),
                BilingualText("BSEB एवं NCERT के संपूर्ण डिजिटल वीडियो लेक्चर बैंक", "Complete BSEB & NCERT Digital Video Lecture Repository")
            ),
            timing = BilingualText("प्रातः 09:00 से सायं 04:00 बजे तक (सोमवार - शनिवार)", "09:00 AM – 04:00 PM (Monday – Saturday)"),
            inCharge = BilingualText("श्री आलोक रंजन तिवारी (ICT एवं कंप्यूटर विभागाध्यक्ष)", "Mr. Alok Ranjan Tiwari (HOD, ICT & Computer Science)"),
            imageRes = R.drawable.img_smart_classroom,
            badgeText = BilingualText("100% डिजिटल कैंपस", "100% Digital Enabled")
        ),
        FacilityItem(
            id = "fac_science_lab",
            title = BilingualText(
                "अत्याधुनिक विज्ञान प्रयोगशालाएं (भौतिकी, रसायन एवं जीव विज्ञान)",
                "Advanced Integrated Science Laboratories (Physics, Chemistry & Bio)"
            ),
            subtitle = BilingualText(
                "प्रयोगात्मक शिक्षा • आधुनिक उपकरण • सुरक्षा मानकों से युक्त",
                "Hands-on Experimentation • Precision Apparatus • Safety Certified"
            ),
            description = BilingualText(
                "कक्षा 9वीं से 12वीं (I.Sc.) के विद्यार्थियों के लिए पृथक भौतिकी, रसायन शास्त्र और जीव विज्ञान प्रयोगशालाएं उपलब्ध हैं। प्रत्येक छात्र को स्वयं प्रयोग करने, सूक्ष्मदर्शी (Microscope) के उपयोग एवं रासायनिक विश्लेषण का व्यावहारिक अनुभव दिया जाता है।",
                "Separate, spacious laboratories for Physics, Chemistry, and Biology allow Class 9–12 students to perform every BSEB and NCERT practical experiment individually with modern optical benches, digital balances, and compound microscopes."
            ),
            highlights = listOf(
                BilingualText("ऑप्टिकल बेंच, वर्नियर कैलिपर्स, सेमीकंडक्टर किट एवं डिजिटल मल्टीमीटर", "Optical Benches, Vernier Callipers, Semiconductor Kits & Digital Multimeters"),
                BilingualText("सुरक्षित फ्यूम हुड, टाइट्रेशन स्टेशन एवं शुद्ध रसायन भंडार", "Safe Fume Hoods, Titration Workstations & Analytical Reagents"),
                BilingualText("हाई-रिज़ॉल्यूशन बायोलॉजिकल माइक्रोस्कोप एवं 3D मानव शरीर मॉडल", "High-Resolution Compound Microscopes & 3D Anatomical Models")
            ),
            timing = BilingualText("प्रातः 10:00 से दोपहर 03:30 बजे तक (प्रायोगिक सत्र)", "10:00 AM – 03:30 PM (Scheduled Practical Periods)"),
            inCharge = BilingualText("डॉ. राजीव नयन मिश्रा (वरिष्ठ प्रवक्ता - भौतिकी)", "Dr. Rajeev Nayan Mishra (Senior Lecturer - Physics)"),
            imageRes = R.drawable.img_science_lab,
            badgeText = BilingualText("I.Sc. उत्कृष्टता केंद्र", "I.Sc. Center of Excellence")
        ),
        FacilityItem(
            id = "fac_sports_ncc",
            title = BilingualText(
                "वीर कुंवर सिंह खेल परिसर, एनसीसी एवं स्काउट-गाइड",
                "Veer Kunwar Singh Sports Complex, NCC & Bharat Scouts"
            ),
            subtitle = BilingualText(
                "400m एथलेटिक्स ट्रैक • क्रिकेट, वॉलीबॉल, कबड्डी • योग एवं फिटनेस",
                "400m Athletics Track • Cricket, Volleyball, Kabaddi • Yoga & NCC"
            ),
            description = BilingualText(
                "भोजपुर-बक्सर की वीर भूमि की परंपरा को आगे बढ़ाते हुए विद्यालय का विशाल खेल मैदान विद्यार्थियों में शारीरिक दक्षता, अनुशासन और नेतृत्व क्षमता का विकास करता है। हमारे छात्र तरंग स्पोर्ट्स मीट एवं राज्य स्तरीय प्रतियोगिताओं में नियमित पदक विजेता हैं।",
                "Honoring the heroic legacy of Shahabad and Buxar, our expansive sports complex nurtures physical fitness, army/police recruitment readiness, and state-level championship talent in athletics, kabaddi, volleyball, and cricket."
            ),
            highlights = listOf(
                BilingualText("बिहार 'तरंग' एवं मशाल खेल कूद प्रतियोगिता प्रशिक्षण केंद्र", "Bihar 'Tarang' & Mashal Sports Championship Training Ground"),
                BilingualText("एनसीसी (NCC) परेड, स्काउट-गाइड एवं सेना/पुलिस शारीरिक तैयारी शिविर", "NCC Parade, Scout-Guide & Defense Physical Fitness Conditioning"),
                BilingualText("छात्राओं के लिए आत्मरक्षा (मार्शल आर्ट/कराटे) एवं योग प्रशिक्षण", "Self-Defense (Karate/Martial Arts) & Morning Yoga for Girl Students")
            ),
            timing = BilingualText("प्रातः 07:30 से 09:00 एवं अपराह्न 02:30 से 04:15 बजे", "07:30 AM – 09:00 AM & 02:30 PM – 04:15 PM"),
            inCharge = BilingualText("श्री विक्रमादित्य सिंह (शारीरिक शिक्षा एवं खेल अनुदेशक)", "Mr. Vikramaditya Singh (Physical Education & Sports Director)"),
            imageRes = R.drawable.img_sports_ground,
            badgeText = BilingualText("जिला चैंपियन कैंपस", "District Sports Champions")
        ),
        FacilityItem(
            id = "fac_library",
            title = BilingualText(
                "देशरत्न डॉ. राजेंद्र प्रसाद केंद्रीय एवं डिजिटल पुस्तकालय",
                "Dr. Rajendra Prasad Central & Digital E-Library"
            ),
            subtitle = BilingualText(
                "12,500+ पुस्तकें • प्रतियोगी परीक्षा कॉर्नर (JEE/NEET/BPSC/CUET)",
                "12,500+ Volumes • Competitive Exam Cell (JEE/NEET/CUET/NDA)"
            ),
            description = BilingualText(
                "शांत और सुव्यवस्थित वाचनालय जहाँ एनसीईआरटी, बिहार बोर्ड पाठ्यपुस्तकें, हिंदी-अंग्रेजी साहित्य, दैनिक समाचार पत्र एवं JEE, NEET, NDA, CUET और रेलवे/डिफेंस प्रतियोगी परीक्षाओं की तैयारी हेतु संदर्भ ग्रंथ निःशुल्क उपलब्ध हैं।",
                "A serene reading sanctuary housing 12,500+ books, NCERT/BSTBPC textbooks, literary classics, newspapers, and a dedicated career guidance corner for JEE, NEET, NDA, and CUET aspirants."
            ),
            highlights = listOf(
                BilingualText("प्रत्येक विद्यार्थी को निःशुल्क बुक-बैंक एवं लाइब्रेरी स्मार्ट कार्ड", "Free Book-Bank & Smart Library Card for every enrolled student"),
                BilingualText("JEE Main, NEET, NDA एवं CUET प्रश्न बैंक और मॉडल पेपर संग्रह", "Dedicated JEE Main, NEET, NDA & CUET Question Bank Collection"),
                BilingualText("दैनिक हिंदी एवं अंग्रेजी समाचार पत्र, विज्ञान प्रगति और रोजगार समाचार", "Daily Hindi & English Newspapers, Science Magazines & Career Bulletins")
            ),
            timing = BilingualText("प्रातः 09:30 से सायं 04:00 बजे तक", "09:30 AM – 04:00 PM"),
            inCharge = BilingualText("श्रीमती प्रिया कुमारी पाण्डेय (पुस्तकालयाध्यक्ष)", "Mrs. Priya Kumari Pandey (Chief Librarian)"),
            imageRes = R.drawable.img_campus_hero,
            badgeText = BilingualText("12,500+ पुस्तक संग्रह", "12,500+ Books & Journals")
        ),
        FacilityItem(
            id = "fac_atal_robotics",
            title = BilingualText(
                "अटल टिंकरिंग, रोबोटिक्स एवं गंगा पर्यावरण नवाचार क्लब",
                "Atal Tinkering, Robotics & Ganga Eco-Innovation Hub"
            ),
            subtitle = BilingualText(
                "सोलर एनर्जी मॉडल • रोबोटिक्स किट • इंस्पायर अवार्ड प्रोजेक्ट",
                "Solar Energy Models • Arduino Robotics • INSPIRE Award Lab"
            ),
            description = BilingualText(
                "ग्रामीण प्रतिभाओं को वैश्विक वैज्ञानिक दृष्टिकोण देने के लिए नवाचार क्लब में विद्यार्थी ड्रोन, जल संरक्षण, स्मार्ट कृषि (Smart Farming for Ganga Belt) और सौर ऊर्जा के कार्यशील मॉडल तैयार करते हैं।",
                "Empowering rural innovators of Gangauli to build real-world science models in smart agriculture, river water quality monitoring, solar energy, and microcontroller robotics for National INSPIRE Awards."
            ),
            highlights = listOf(
                BilingualText("राष्ट्रीय इंस्पायर अवार्ड (INSPIRE-MANAK) एवं बाल विज्ञान कांग्रेस मार्गदर्शन", "National INSPIRE-MANAK Award & Children's Science Congress Mentorship"),
                BilingualText("सेंसर आधारित स्मार्ट सिंचाई एवं गंगा जल शुद्धता परीक्षण किट", "Sensor-based Smart Irrigation & Ganga Water Quality Testing Kits"),
                BilingualText("10kW रूफटॉप सोलर पैनल युक्त हरित एवं ऊर्जा-आत्मनिर्भर परिसर", "10kW Rooftop Solar Powered Green Campus Infrastructure")
            ),
            timing = BilingualText("मंगलवार, गुरुवार एवं शनिवार (दोपहर 01:30 - 03:30)", "Tue, Thu & Sat (01:30 PM – 03:30 PM)"),
            inCharge = BilingualText("श्री संदीप कुमार वर्मा (विज्ञान एवं नवाचार समन्वयक)", "Mr. Sandeep Kumar Verma (Science & Innovation Coordinator)"),
            imageRes = R.drawable.img_smart_classroom,
            badgeText = BilingualText("नवाचार एवं रोबोटिक्स", "STEM & Robotics Hub")
        ),
        FacilityItem(
            id = "fac_girls_safety",
            title = BilingualText(
                "मुख्यमंत्री किशोरी स्वास्थ्य, सुरक्षा कवच एवं स्वच्छ पेयजल परिसर",
                "Girls' Empowerment, CCTV Safe Campus & RO Water Complex"
            ),
            subtitle = BilingualText(
                "24x7 CCTV निगरानी • गार्गी कॉर्नर • आरओ पेयजल एवं प्राथमिक चिकित्सा",
                "24x7 CCTV Surveillance • Gargi Girls' Lounge • UV+RO Water & First Aid"
            ),
            description = BilingualText(
                "गंगौली एवं सिमरी प्रखंड की बेटियों की उच्च शिक्षा को पूर्णतः सुरक्षित और सम्मानजनक बनाने हेतु परिसर में 32 HD CCTV कैमरे, पृथक स्वच्छ प्रसाधन, वेडिंग मशीन युक्त किशोरी स्वास्थ्य कक्ष एवं महिला परामर्शदात्री की व्यवस्था है।",
                "Designed as a model safe campus for daughters of Simri Block with 32 HD CCTV cameras, dedicated Girls' Common Room, Sanitary Pad Vending & Wellness Unit, First-Aid bay, and chilled RO drinking water stations."
            ),
            highlights = listOf(
                BilingualText("32 नाइट-विजन HD CCTV कैमरों द्वारा संपूर्ण कैंपस की निगरानी", "32 HD CCTV Cameras monitoring all corridors and entry gates"),
                BilingualText("छात्राओं हेतु पृथक कॉमन रूम, सैनिटरी वेंडिंग मशीन एवं स्वास्थ्य परामर्श", "Dedicated Girls' Common Room, Health Hygiene Unit & Female Counselor"),
                BilingualText("1000 लीटर/घंटा क्षमता का शुद्ध शीतल RO पेयजल संयंत्र", "1000 LPH Commercial UV+RO Purified Chilled Drinking Water Plant")
            ),
            timing = BilingualText("संपूर्ण विद्यालय अवधि में सक्रिय", "Active Throughout Campus Hours"),
            inCharge = BilingualText("डॉ. वंदना उपाध्याय (महिला प्रकोष्ठ एवं छात्रा कल्याण प्रभारी)", "Dr. Vandana Upadhyay (Girls' Welfare & Safety Cell Coordinator)"),
            imageRes = R.drawable.img_campus_hero,
            badgeText = BilingualText("सुरक्षित एवं हरित परिसर", "100% Safe & Hygienic")
        )
    )

    val academicStreams = listOf(
        AcademicStream(
            id = "stream_sec",
            classRange = BilingualText("कक्षा 9वीं एवं 10वीं (माध्यमिक / मैट्रिक)", "Class 9th & 10th (Secondary / Matriculation)"),
            streamTitle = BilingualText("माध्यमिक शिक्षा प्रभाग (BSEB पटना)", "Secondary Education Wing (BSEB Patna)"),
            code = "MATRIC-73045",
            seats = 320,
            medium = BilingualText("हिंदी एवं अंग्रेजी (द्विभाषी माध्यम)", "Hindi & English (Bilingual Medium)"),
            coreSubjects = listOf(
                BilingualText("गणित (Mathematics)", "Mathematics"),
                BilingualText("विज्ञान — भौतिकी, रसायन, जीव विज्ञान (Science)", "Integrated Science (Phy, Chem, Bio)"),
                BilingualText("सामाजिक विज्ञान — इतिहास, भूगोल, नागरिक, अर्थशास्त्र", "Social Science (Hist, Geo, Civics, Eco)"),
                BilingualText("मातृभाषा हिंदी (गोधूलि एवं वर्णिका)", "Hindi Literature & Grammar"),
                BilingualText("अंग्रेजी (Panorama English)", "English Language & Literature"),
                BilingualText("द्वितीय भारतीय भाषा — संस्कृत (पीयूषम्)", "Sanskrit (Second Indian Language)")
            ),
            electiveSubjects = listOf(
                BilingualText("उच्च गणित (Advanced Mathematics)", "Advanced Mathematics"),
                BilingualText("कंप्यूटर अनुप्रयोग एवं सूचना प्रौद्योगिकी", "Computer Applications & IT"),
                BilingualText("ललित कला एवं संगीत", "Fine Arts & Music")
            ),
            careerPathways = BilingualText(
                "मैट्रिक बोर्ड में सर्वोच्च अंक, पॉलिटेक्निक, आईटीआई, एनटीएसई एवं उच्च माध्यमिक (+2) हेतु मजबूत आधार।",
                "Strong foundation for State Board Merit List, Polytechnic, NTSE, Olympiad, and Senior Secondary (+2) streams."
            ),
            hodName = BilingualText("श्री सत्येंद्र नारायण सिंह (प्रभारी - माध्यमिक खंड)", "Mr. Satyendra Narayan Singh (In-charge, Secondary Wing)")
        ),
        AcademicStream(
            id = "stream_isc",
            classRange = BilingualText("कक्षा 11वीं एवं 12वीं (उच्च माध्यमिक / इंटरमीडिएट)", "Class 11th & 12th (+2 Senior Secondary)"),
            streamTitle = BilingualText("विज्ञान संकाय — I.Sc. (PCM / PCB / PCMB)", "Faculty of Science — I.Sc. (PCM / PCB / PCMB)"),
            code = "ISC-73045",
            seats = 180,
            medium = BilingualText("हिंदी एवं अंग्रेजी माध्यम", "Hindi & English Medium"),
            coreSubjects = listOf(
                BilingualText("भौतिक विज्ञान (Physics + Lab Practical)", "Physics (Theory 70 + Practical 30)"),
                BilingualText("रसायन विज्ञान (Chemistry + Lab Practical)", "Chemistry (Theory 70 + Practical 30)"),
                BilingualText("गणित (Mathematics — 100 Marks)", "Mathematics (Calculus, Algebra, Vectors)"),
                BilingualText("जीव विज्ञान (Biology — Botany & Zoology)", "Biology (Botany & Zoology + Practical)"),
                BilingualText("अनिवार्य भाषा — हिंदी (दिगंत) एवं अंग्रेजी (Rainbow)", "Compulsory Languages — Hindi & English (100 Marks each)")
            ),
            electiveSubjects = listOf(
                BilingualText("कंप्यूटर साइंस एवं C++/Python प्रोग्रामिंग", "Computer Science & Python/C++"),
                BilingualText("अतिरिक्त विषय के रूप में गणित अथवा जीव विज्ञान", "Additional Subject: Math or Biology (PCMB)")
            ),
            careerPathways = BilingualText(
                "IIT-JEE, NEET (मेडिकल), NDA (रक्षा सेवा), B.Sc. (Hons), इंजीनियरिंग, नर्सिंग एवं वैज्ञानिक अनुसंधान।",
                "Direct preparation for IIT-JEE, NEET Medical, NDA Defense Academy, B.Sc. Honors, and Engineering careers."
            ),
            hodName = BilingualText("डॉ. राजीव नयन मिश्रा (विभागाध्यक्ष - विज्ञान संकाय)", "Dr. Rajeev Nayan Mishra (HOD, Science Faculty)")
        ),
        AcademicStream(
            id = "stream_ia",
            classRange = BilingualText("कक्षा 11वीं एवं 12वीं (उच्च माध्यमिक / इंटरमीडिएट)", "Class 11th & 12th (+2 Senior Secondary)"),
            streamTitle = BilingualText("कला एवं मानविकी संकाय — I.A. (Arts & Humanities)", "Faculty of Arts & Humanities — I.A."),
            code = "IA-73045",
            seats = 240,
            medium = BilingualText("हिंदी एवं अंग्रेजी माध्यम", "Hindi & English Medium"),
            coreSubjects = listOf(
                BilingualText("इतिहास (History — भारत एवं विश्व इतिहास)", "History (Indian & World Civilization)"),
                BilingualText("राजनीति विज्ञान (Political Science & Indian Constitution)", "Political Science & Governance"),
                BilingualText("भूगोल (Geography + प्रायोगिक मानचित्रण)", "Geography (Theory + Cartography Practical)"),
                BilingualText("अर्थशास्त्र (Economics — व्यष्टि एवं समष्टि)", "Economics (Micro & Macroeconomics)"),
                BilingualText("अनिवार्य भाषा — हिंदी एवं अंग्रेजी", "Compulsory Languages — Hindi & English")
            ),
            electiveSubjects = listOf(
                BilingualText("मनोविज्ञान (Psychology + Practical)", "Psychology (with Practical)"),
                BilingualText("गृह विज्ञान (Home Science) एवं समाजशास्त्र (Sociology)", "Home Science & Sociology"),
                BilingualText("संगीत (Music Vocal/Instrumental)", "Classical Music & Fine Arts")
            ),
            careerPathways = BilingualText(
                "UPSC / BPSC सिविल सेवा, न्यायिक सेवा (CLAT/LLB), पत्रकारिता, शिक्षण (B.Ed/CTET) एवं केंद्रीय विश्वविद्यालय (CUET)।",
                "Ideal launchpad for UPSC/BPSC Civil Services, Law (CLAT), Central Universities (CUET), Journalism, and Teaching."
            ),
            hodName = BilingualText("डॉ. अरुण कुमार चौबे (विभागाध्यक्ष - कला एवं मानविकी)", "Dr. Arun Kumar Choubey (HOD, Arts & Humanities)")
        ),
        AcademicStream(
            id = "stream_icom",
            classRange = BilingualText("कक्षा 11वीं एवं 12वीं (वाणिज्य एवं व्यावसायिक)", "Class 11th & 12th (Commerce & Vocational)"),
            streamTitle = BilingualText("वाणिज्य एवं सूचना प्रौद्योगिकी — I.Com. & Vocational", "Faculty of Commerce & IT Vocational — I.Com."),
            code = "ICOM-73045",
            seats = 120,
            medium = BilingualText("हिंदी एवं अंग्रेजी माध्यम", "Hindi & English Medium"),
            coreSubjects = listOf(
                BilingualText("लेखाशास्त्र (Accountancy)", "Accountancy & Financial Reporting"),
                BilingualText("व्यावसायिक अध्ययन (Business Studies - BST)", "Business Studies & Management"),
                BilingualText("उद्यमिता (Entrepreneurship - EPS)", "Entrepreneurship (EPS + Practical)"),
                BilingualText("अर्थशास्त्र (Economics)", "Business Economics"),
                BilingualText("हिंदी एवं अंग्रेजी (Compulsory Languages)", "Hindi & English")
            ),
            electiveSubjects = listOf(
                BilingualText("टैली प्राइम, जीएसटी एवं डिजिटल बैंकिंग कौशल", "Tally Prime, GST Filing & Digital Banking Skills"),
                BilingualText("सूचना प्रौद्योगिकी (IT/ITES व्यावसायिक ट्रेड)", "IT/ITES Vocational Skill Certification")
            ),
            careerPathways = BilingualText(
                "चार्टर्ड अकाउंटेंट (CA/CS), बैंकिंग (IBPS/SBI PO), B.Com, BBA, MBA एवं स्वरोजगार उद्यमिता।",
                "Pathways to Chartered Accountancy (CA/CS), Banking & Finance (IBPS/RBI), BBA/MBA, and FinTech."
            ),
            hodName = BilingualText("श्री मनीष कुमार श्रीवास्तव (प्रभारी - वाणिज्य एवं कौशल विकास)", "Mr. Manish Kumar Srivastava (In-charge, Commerce & Skill Cell)")
        )
    )

    fun getTimetableFor(classId: String, dayId: String): List<TimetableSlot> {
        val isWeekendSat = dayId == "SAT"
        return when (classId) {
            "CLASS_10" -> listOf(
                TimetableSlot(1, "09:30 - 10:15", BilingualText("प्रार्थना सभा, बिहार राज्य गीत एवं सामान्य ज्ञान", "Morning Assembly, Bihar State Song & Daily GK"), BilingualText("सभी शिक्षक वृंद", "All Faculty Members"), BilingualText("मुख्य प्रांगण (Main Courtyard)", "Main Courtyard")),
                TimetableSlot(2, "10:15 - 11:00", BilingualText("गणित (त्रिकोणमिति एवं निर्देशांक ज्यामिति)", "Mathematics (Trigonometry & Coordinate Geometry)"), BilingualText("श्री सत्येंद्र नारायण सिंह", "Mr. Satyendra Narayan Singh"), BilingualText("कक्ष संख्या 102", "Room 102")),
                TimetableSlot(3, "11:00 - 11:45", BilingualText("भौतिकी एवं रसायन (उन्नयन स्मार्ट क्लास)", "Physics & Chemistry (Unnayan Smart Class)"), BilingualText("डॉ. राजीव नयन मिश्रा", "Dr. Rajeev Nayan Mishra"), BilingualText("उन्नयन स्मार्ट हॉल-1", "Unnayan Smart Hall-1"), true),
                TimetableSlot(4, "11:45 - 12:30", BilingualText("अंग्रेजी (Panorama & Grammar Writing)", "English (Literature & Grammar)"), BilingualText("श्रीमती नेहा सिन्हा", "Mrs. Neha Sinha"), BilingualText("कक्ष संख्या 102", "Room 102")),
                TimetableSlot(5, "12:30 - 01:10", BilingualText("मध्याह्न विश्राम एवं पोषण/खेल संवाद", "Midday Recess & Student Wellness Break"), BilingualText("अनुशासन समिति", "Student Welfare Team"), BilingualText("कैंपस उद्यान", "Campus Green Zone")),
                TimetableSlot(6, "01:10 - 01:55", BilingualText("सामाजिक विज्ञान (इतिहास एवं आपदा प्रबंधन)", "Social Science (History & Disaster Mgmt)"), BilingualText("डॉ. अरुण कुमार चौबे", "Dr. Arun Kumar Choubey"), BilingualText("कक्ष संख्या 102", "Room 102")),
                TimetableSlot(
                    7,
                    "01:55 - 02:45",
                    if (isWeekendSat) BilingualText("बाल संसद, क्विज प्रतियोगिता एवं सुरक्षित शनिवार", "Bal Sansad, Quiz & 'Surakshit Shanivar' Drill")
                    else BilingualText("जीव विज्ञान प्रायोगिक / संस्कृत व्याकरण", "Biology Practical / Sanskrit Grammar"),
                    BilingualText("डॉ. वंदना उपाध्याय", "Dr. Vandana Upadhyay"),
                    BilingualText("विज्ञान प्रयोगशाला / कक्ष 102", "Science Lab / Room 102"),
                    true
                ),
                TimetableSlot(8, "02:45 - 03:30", BilingualText("मिशन दक्ष / बोर्ड मॉडल पेपर अभ्यास एवं खेलकूद", "Mission Daksh / Board Model Test & Sports"), BilingualText("श्री विक्रमादित्य सिंह", "Mr. Vikramaditya Singh"), BilingualText("खेल परिसर / स्मार्ट रूम", "Sports Ground / Smart Hall"))
            )
            "CLASS_12_SCI" -> listOf(
                TimetableSlot(1, "09:30 - 10:15", BilingualText("चेतना सत्र एवं करंट अफेयर्स संवाद", "Morning चेतना Session & Current Affairs"), BilingualText("प्राचार्य एवं वरिष्ठ प्रवक्ता", "Principal & Senior Lecturers"), BilingualText("मुख्य प्रांगण", "Main Assembly Plaza")),
                TimetableSlot(2, "10:15 - 11:05", BilingualText("भौतिक विज्ञान — स्थिरवैद्युतिकी एवं प्रकाशिकी (Physics)", "Physics — Electrostatics & Ray Optics"), BilingualText("डॉ. राजीव नयन मिश्रा", "Dr. Rajeev Nayan Mishra"), BilingualText("विज्ञान व्याख्यान कक्ष-201", "Science Lecture Hall 201")),
                TimetableSlot(3, "11:05 - 11:55", BilingualText("रसायन शास्त्र — कार्बनिक एवं भौतिक रसायन (Chemistry)", "Chemistry — Organic & Chemical Kinetics"), BilingualText("श्री संदीप कुमार वर्मा", "Mr. Sandeep Kumar Verma"), BilingualText("विज्ञान व्याख्यान कक्ष-201", "Science Lecture Hall 201")),
                TimetableSlot(4, "11:55 - 12:45", BilingualText("गणित (Calculus) / जीव विज्ञान (Genetics)", "Mathematics (Calculus) / Biology (Genetics)"), BilingualText("श्री आलोक रंजन / डॉ. वंदना उपाध्याय", "Mr. Alok Ranjan / Dr. Vandana Upadhyay"), BilingualText("कक्ष 201 एवं बायो हॉल 202", "Room 201 & Bio Hall 202")),
                TimetableSlot(5, "12:45 - 01:20", BilingualText("मध्याह्न भोजन अवकाश एवं ई-लाइब्रेरी भ्रमण", "Lunch Break & Digital Library Reading"), BilingualText("श्रीमती प्रिया कुमारी पाण्डेय", "Mrs. Priya Kumari Pandey"), BilingualText("केंद्रीय पुस्तकालय", "Central Library")),
                TimetableSlot(6, "01:20 - 02:20", BilingualText("भौतिकी / रसायन प्रायोगिक (Lab Practical Batch A & B)", "Physics / Chemistry Lab Practical (Batch A & B)"), BilingualText("डॉ. राजीव मिश्रा एवं श्री संदीप वर्मा", "Dr. Rajeev Mishra & Mr. Sandeep Verma"), BilingualText("एकीकृत विज्ञान प्रयोगशाला", "Integrated Science Lab"), true),
                TimetableSlot(
                    7,
                    "02:20 - 03:15",
                    if (isWeekendSat) BilingualText("JEE / NEET / NDA साप्ताहिक ओएमआर (OMR) टेस्ट सीरीज", "Weekly JEE / NEET / NDA OMR Mock Test Series")
                    else BilingualText("अंग्रेजी (Rainbow Part-II) एवं हिंदी (दिगंत भाग-2)", "English (Rainbow-II) & Hindi (Digant-II)"),
                    BilingualText("श्रीमती नेहा सिन्हा एवं डॉ. रमाकांत ओझा", "Mrs. Neha Sinha & Dr. Ramakant Ojha"),
                    BilingualText("उन्नयन स्मार्ट हॉल-2", "Unnayan Smart Hall-2"),
                    true
                )
            )
            "CLASS_12_ARTS" -> listOf(
                TimetableSlot(1, "09:30 - 10:15", BilingualText("प्रार्थना सभा, संविधान की प्रस्तावना एवं समाचार वाचन", "Assembly, Preamble Reading & News Analysis"), BilingualText("सभी संकाय सदस्य", "All Faculty Members"), BilingualText("मुख्य प्रांगण", "Main Courtyard")),
                TimetableSlot(2, "10:15 - 11:05", BilingualText("इतिहास — आधुनिक भारत एवं विश्व सभ्यता", "History — Modern India & World Heritage"), BilingualText("डॉ. अरुण कुमार चौबे", "Dr. Arun Kumar Choubey"), BilingualText("कला संकाय कक्ष-301", "Arts Lecture Hall 301")),
                TimetableSlot(3, "11:05 - 11:55", BilingualText("राजनीति विज्ञान — भारतीय राजव्यवस्था एवं अंतरराष्ट्रीय संबंध", "Political Science — Indian Polity & International Relations"), BilingualText("श्री रमाकांत ओझा", "Mr. Ramakant Ojha"), BilingualText("कला संकाय कक्ष-301", "Arts Lecture Hall 301")),
                TimetableSlot(4, "11:55 - 12:45", BilingualText("भूगोल (मानव भूगोल एवं मानचित्र प्रायोगिक)", "Geography (Human Geography & GIS Mapping)"), BilingualText("श्रीमती कंचन लता कुमारी", "Mrs. Kanchan Lata Kumari"), BilingualText("भूगोल प्रयोगशाला-302", "Geography Lab 302"), true),
                TimetableSlot(5, "12:45 - 01:20", BilingualText("मध्याह्न अवकाश", "Midday Recess"), BilingualText("छात्र कल्याण प्रकोष्ठ", "Student Welfare Cell"), BilingualText("कैंपस परिसर", "Campus Courtyard")),
                TimetableSlot(6, "01:20 - 02:15", BilingualText("अर्थशास्त्र — राष्ट्रीय आय एवं बजट विश्लेषण", "Economics — National Income & Public Finance"), BilingualText("श्री मनीष कुमार श्रीवास्तव", "Mr. Manish Kumar Srivastava"), BilingualText("कला संकाय कक्ष-301", "Arts Lecture Hall 301")),
                TimetableSlot(
                    7,
                    "02:15 - 03:15",
                    if (isWeekendSat) BilingualText("BPSC / CUET सामान्य अध्ययन क्विज एवं वाद-विवाद सभा", "BPSC / CUET General Studies Quiz & Debate")
                    else BilingualText("ICT कंप्यूटर कौशल एवं हिंदी/अंग्रेजी साहित्य", "ICT Digital Skills & Literature"),
                    BilingualText("श्री आलोक रंजन तिवारी", "Mr. Alok Ranjan Tiwari"),
                    BilingualText("ICT कंप्यूटर लैब", "ICT Computer Lab"),
                    true
                )
            )
            else -> listOf(
                TimetableSlot(1, "09:30 - 10:15", BilingualText("चेतना सत्र, योग एवं नैतिक शिक्षा", "Morning Assembly, Yoga & Moral Values"), BilingualText("सभी शिक्षक वृंद", "All Faculty Members"), BilingualText("मुख्य प्रांगण", "Main Courtyard")),
                TimetableSlot(2, "10:15 - 11:00", BilingualText("गणित (बीजगणित एवं ज्यामिति फाउंडेशन)", "Mathematics (Algebra & Geometry Foundation)"), BilingualText("श्री सत्येंद्र नारायण सिंह", "Mr. Satyendra Narayan Singh"), BilingualText("कक्ष संख्या 101", "Room 101")),
                TimetableSlot(3, "11:00 - 11:45", BilingualText("सामान्य विज्ञान (स्मार्ट बोर्ड एनिमेशन सत्र)", "General Science (Smartboard Visual Session)"), BilingualText("श्री संदीप कुमार वर्मा", "Mr. Sandeep Kumar Verma"), BilingualText("उन्नयन स्मार्ट हॉल-1", "Unnayan Smart Hall-1"), true),
                TimetableSlot(4, "11:45 - 12:30", BilingualText("हिंदी साहित्य (गोधूलि भाग-1) एवं रचनात्मक लेखन", "Hindi Literature & Creative Writing"), BilingualText("श्री रमाकांत ओझा", "Mr. Ramakant Ojha"), BilingualText("कक्ष संख्या 101", "Room 101")),
                TimetableSlot(5, "12:30 - 01:10", BilingualText("मध्याह्न अवकाश", "Midday Break"), BilingualText("विद्यालय परिवार", "School Administration"), BilingualText("विद्यालय परिसर", "Campus Plaza")),
                TimetableSlot(6, "01:10 - 01:55", BilingualText("अंग्रेजी संभाषण एवं व्याकरण (Spoken English & Grammar)", "Spoken English & Functional Grammar"), BilingualText("श्रीमती नेहा सिन्हा", "Mrs. Neha Sinha"), BilingualText("भाषा प्रयोगशाला / कक्ष 101", "Language Lab / Room 101")),
                TimetableSlot(7, "01:55 - 02:45", BilingualText("कंप्यूटर बेसिक्स एवं अटल रोबोटिक्स कार्यशाला", "Computer Basics & Atal Tinkering Workshop"), BilingualText("श्री आलोक रंजन तिवारी", "Mr. Alok Ranjan Tiwari"), BilingualText("ICT लैब", "ICT Computer Lab"), true),
                TimetableSlot(8, "02:45 - 03:30", BilingualText("खेलकूद, स्काउट-गाइड एवं पुस्तकालय स्वाध्याय", "Sports, Bharat Scouts & Library Reading"), BilingualText("श्री विक्रमादित्य सिंह", "Mr. Vikramaditya Singh"), BilingualText("वीर कुंवर सिंह खेल मैदान", "Sports Complex"))
            )
        }
    }

    val facultyMembers = listOf(
        FacultyMember(
            id = "fac_1",
            name = BilingualText("डॉ. राकेश कुमार पाण्डेय", "Dr. Rakesh Kumar Pandey"),
            designation = BilingualText("प्राचार्य (Principal / Headmaster)", "Principal & Head of Institution"),
            department = "Administration",
            subject = BilingualText("शैक्षणिक नेतृत्व एवं भौतिक विज्ञान", "Educational Leadership & Physics"),
            qualification = BilingualText("M.Sc. (Physics), Ph.D., B.Ed. (BPSC Senior Cadre)", "M.Sc. (Physics), Ph.D., B.Ed. (BPSC Senior Cadre)"),
            experience = BilingualText("22 वर्ष का शैक्षणिक एवं प्रशासनिक अनुभव", "22 Years Academic & Administrative Experience"),
            message = BilingualText(
                "हमारा संकल्प गंगौली और सिमरी अंचल के प्रत्येक विद्यार्थी को पटना, दिल्ली और विश्व के सर्वश्रेष्ठ विद्यालयों के समकक्ष आधुनिक, संस्कारयुक्त एवं तकनीक-संपन्न शिक्षा प्रदान करना है।",
                "Our mission is to empower every child of Gangauli and Simri Block with world-class digital education, scientific temper, and deep moral roots."
            ),
            phoneExt = "06183-245101"
        ),
        FacultyMember(
            id = "fac_2",
            name = BilingualText("डॉ. राजीव नयन मिश्रा", "Dr. Rajeev Nayan Mishra"),
            designation = BilingualText("वरिष्ठ प्रवक्ता (PGT — विज्ञान संकाय प्रमुख)", "Senior Lecturer (PGT — Head of Science)"),
            department = "Science",
            subject = BilingualText("भौतिक विज्ञान (Physics - Class 11 & 12)", "Physics (+2 Intermediate)"),
            qualification = BilingualText("M.Sc. (Gold Medalist), Ph.D., B.Ed., CSIR-NET", "M.Sc. (Gold Medalist), Ph.D., B.Ed., CSIR-NET"),
            experience = BilingualText("14 वर्ष (BPSC PGT चयनित)", "14 Years (BPSC PGT Cadre)"),
            message = BilingualText(
                "प्रयोगशाला में प्रत्येक सिद्धांत को प्रत्यक्ष देखकर समझना ही सच्चे विज्ञान की शुरुआत है।",
                "Experiencing laws of nature firsthand in the laboratory is the true beginning of scientific inquiry."
            ),
            phoneExt = "Ext-201"
        ),
        FacultyMember(
            id = "fac_3",
            name = BilingualText("डॉ. अरुण कुमार चौबे", "Dr. Arun Kumar Choubey"),
            designation = BilingualText("वरिष्ठ प्रवक्ता (PGT — कला संकाय प्रमुख)", "Senior Lecturer (PGT — Head of Arts)"),
            department = "Arts",
            subject = BilingualText("इतिहास एवं नागरिक शास्त्र (History & Polity)", "History & Political Science"),
            qualification = BilingualText("M.A. (History), Ph.D. (BHU), B.Ed., UGC-NET", "M.A. (History), Ph.D. (BHU), B.Ed., UGC-NET"),
            experience = BilingualText("16 वर्ष का शिक्षण अनुभव", "16 Years Teaching Experience"),
            message = BilingualText(
                "बक्सर की ऐतिहासिक भूमि के छात्र सिविल सेवा और राष्ट्र निर्माण में अग्रणी भूमिका निभा रहे हैं।",
                "Students from the historic soil of Buxar are destined to lead in Civil Services and nation-building."
            ),
            phoneExt = "Ext-301"
        ),
        FacultyMember(
            id = "fac_4",
            name = BilingualText("डॉ. वंदना उपाध्याय", "Dr. Vandana Upadhyay"),
            designation = BilingualText("प्रवक्ता (PGT — जीव विज्ञान एवं महिला प्रकोष्ठ)", "Lecturer (PGT — Biology & Girls' Welfare)"),
            department = "Science",
            subject = BilingualText("जीव विज्ञान एवं पर्यावरण (Biology & Biotech)", "Biology & Environmental Science"),
            qualification = BilingualText("M.Sc. (Botany), Ph.D., B.Ed., STET Qualified", "M.Sc. (Botany), Ph.D., B.Ed., STET Qualified"),
            experience = BilingualText("11 वर्ष का अनुभव", "11 Years Experience"),
            message = BilingualText(
                "हमारी छात्राएं आज NEET, नर्सिंग और विज्ञान के हर क्षेत्र में नया कीर्तिमान स्थापित कर रही हैं।",
                "Our girl students are setting new benchmarks in NEET, Biotechnology, and higher education every year."
            ),
            phoneExt = "Ext-204"
        ),
        FacultyMember(
            id = "fac_5",
            name = BilingualText("श्री आलोक रंजन तिवारी", "Mr. Alok Ranjan Tiwari"),
            designation = BilingualText("कंप्यूटर साइंस प्रवक्ता (PGT — ICT एवं स्मार्ट क्लास)", "PGT Computer Science & Smart Class Coordinator"),
            department = "Science",
            subject = BilingualText("कंप्यूटर साइंस, कोडिंग एवं AI साक्षरता", "Computer Science, Python & AI Literacy"),
            qualification = BilingualText("M.C.A., B.Tech (CSE), STET & BPSC TRE Qualified", "M.C.A., B.Tech (CSE), STET & BPSC TRE Qualified"),
            experience = BilingualText("9 वर्ष (डिजिटल शिक्षा विशेषज्ञ)", "9 Years (Digital Learning Specialist)"),
            message = BilingualText(
                "गंगौली के विद्यार्थियों को कोडिंग, आर्टिफिशियल इंटेलिजेंस और डिजिटल भविष्य के लिए तैयार करना हमारा लक्ष्य है।",
                "Equipping students of Gangauli with Python coding, digital fluency, and future-ready technology skills."
            ),
            phoneExt = "Ext-108"
        ),
        FacultyMember(
            id = "fac_6",
            name = BilingualText("श्री सत्येंद्र नारायण सिंह", "Mr. Satyendra Narayan Singh"),
            designation = BilingualText("वरिष्ठ माध्यमिक शिक्षक (TGT — गणित)", "Senior Secondary Teacher (TGT — Mathematics)"),
            department = "Secondary",
            subject = BilingualText("उच्च एवं माध्यमिक गणित (Mathematics)", "Secondary & Senior Mathematics"),
            qualification = BilingualText("M.Sc. (Mathematics), B.Ed., BPSC Qualified", "M.Sc. (Mathematics), B.Ed., BPSC Qualified"),
            experience = BilingualText("15 वर्ष का अनुभव", "15 Years Experience"),
            message = BilingualText(
                "वैदिक गणित से लेकर आधुनिक कैलकुलस तक, हर छात्र में गणित का भय समाप्त कर रुचि जगाना हमारी प्राथमिकता है।",
                "Transforming mathematics from a feared subject into every student's strongest scoring asset."
            ),
            phoneExt = "Ext-102"
        ),
        FacultyMember(
            id = "fac_7",
            name = BilingualText("श्रीमती नेहा सिन्हा", "Mrs. Neha Sinha"),
            designation = BilingualText("प्रवक्ता (PGT — अंग्रेजी साहित्य एवं भाषा लैब)", "Lecturer (PGT — English & Language Lab)"),
            department = "Languages",
            subject = BilingualText("अंग्रेजी साहित्य एवं स्पोकन इंग्लिश", "English Literature & Communication Skills"),
            qualification = BilingualText("M.A. (English), B.Ed., PGCTE (EFLU)", "M.A. (English), B.Ed., PGCTE (EFLU)"),
            experience = BilingualText("10 वर्ष का अनुभव", "10 Years Experience"),
            message = BilingualText(
                "मातृभाषा पर गर्व के साथ-साथ धाराप्रवाह अंग्रेजी संवाद क्षमता हमारे विद्यार्थियों को वैश्विक आत्मविश्वास देती है।",
                "Combining pride in our mother tongue with fluent English communication gives our students global confidence."
            ),
            phoneExt = "Ext-105"
        ),
        FacultyMember(
            id = "fac_8",
            name = BilingualText("श्री विक्रमादित्य सिंह", "Mr. Vikramaditya Singh"),
            designation = BilingualText("शारीरिक शिक्षा एवं एनसीसी/स्काउट प्रभारी", "Physical Education & NCC / Sports Officer"),
            department = "Sports",
            subject = BilingualText("खेलकूद, एथलेटिक्स, योग एवं आत्मरक्षा", "Athletics, Team Sports, Yoga & NCC"),
            qualification = BilingualText("M.P.Ed., NIS Athletics Diploma, NCC 'C' Cert.", "M.P.Ed., NIS Athletics Diploma, NCC 'C' Cert."),
            experience = BilingualText("12 वर्ष का अनुभव", "12 Years Experience"),
            message = BilingualText(
                "स्वस्थ शरीर में ही प्रखर मस्तिष्क का वास होता है — खेल मैदान से ही अनुशासन और विजय की आदत बनती है।",
                "Discipline forged on the sports field builds champions in both board examinations and life."
            ),
            phoneExt = "Ext-401"
        )
    )

    val topperStudents = listOf(
        TopperStudent(
            id = "top_1",
            name = BilingualText("आदित्य कुमार राय", "Aditya Kumar Rai"),
            fatherName = BilingualText("श्री मनोज कुमार राय", "Mr. Manoj Kumar Rai"),
            streamClass = BilingualText("कक्षा 12वीं विज्ञान (I.Sc. - PCM)", "Class 12 Science (I.Sc. - PCM)"),
            percentage = "94.8%",
            marksObtained = "474 / 500",
            achievementBadge = BilingualText("🥇 बक्सर जिला टॉपर रैंक-2 • गणित में 99/100", "🥇 Buxar District Rank-2 • 99/100 in Math"),
            village = BilingualText("ग्राम - गंगौली, प्रखंड - सिमरी", "Vill - Gangauli, Block - Simri"),
            year = "2026",
            rollNo = "260101"
        ),
        TopperStudent(
            id = "top_2",
            name = BilingualText("पल्लवी कुमारी पाण्डेय", "Pallavi Kumari Pandey"),
            fatherName = BilingualText("श्री संजय पाण्डेय", "Mr. Sanjay Pandey"),
            streamClass = BilingualText("कक्षा 12वीं विज्ञान (I.Sc. - PCB)", "Class 12 Science (I.Sc. - PCB)"),
            percentage = "93.6%",
            marksObtained = "468 / 500",
            achievementBadge = BilingualText("🏆 सिमरी प्रखंड छात्रा टॉपर • कन्या उत्थान ₹25,000", "🏆 Simri Block Girls Topper • Kanya Utthan Scholar"),
            village = BilingualText("ग्राम - गंगौली (पूरब टोला), सिमरी", "Vill - Gangauli East, Simri"),
            year = "2026",
            rollNo = "260102"
        ),
        TopperStudent(
            id = "top_3",
            name = BilingualText("प्रियांशु कुमार ओझा", "Priyanshu Kumar Ojha"),
            fatherName = BilingualText("श्री विनोद कुमार ओझा", "Mr. Vinod Kumar Ojha"),
            streamClass = BilingualText("कक्षा 10वीं (मैट्रिक बोर्ड - BSEB)", "Class 10 Matriculation (BSEB)"),
            percentage = "95.2%",
            marksObtained = "476 / 500",
            achievementBadge = BilingualText("🌟 विद्यालय मैट्रिक टॉपर • विज्ञान में 98/100", "🌟 School Matric Topper • 98/100 in Science"),
            village = BilingualText("ग्राम - सिमरी, बक्सर", "Vill - Simri, Buxar"),
            year = "2026",
            rollNo = "260103"
        ),
        TopperStudent(
            id = "top_4",
            name = BilingualText("साक्षी कुमारी चौबे", "Sakshi Kumari Choubey"),
            fatherName = BilingualText("श्री दिवाकर चौबे", "Mr. Diwakar Choubey"),
            streamClass = BilingualText("कक्षा 12वीं कला संकाय (I.A. Humanities)", "Class 12 Arts & Humanities (I.A.)"),
            percentage = "91.4%",
            marksObtained = "457 / 500",
            achievementBadge = BilingualText("🎖️ कला संकाय टॉपर • इतिहास में 96 अंक", "🎖️ Arts Stream Topper • 96/100 in History"),
            village = BilingualText("ग्राम - कठार, सिमरी, बक्सर", "Vill - Kathar, Simri, Buxar"),
            year = "2026",
            rollNo = "260104"
        ),
        TopperStudent(
            id = "top_5",
            name = BilingualText("आयुष कुमार ठाकुर", "Ayush Kumar Thakur"),
            fatherName = BilingualText("श्री अरविंद ठाकुर", "Mr. Arvind Thakur"),
            streamClass = BilingualText("कक्षा 10वीं (मैट्रिक बोर्ड - BSEB)", "Class 10 Matriculation (BSEB)"),
            percentage = "92.8%",
            marksObtained = "464 / 500",
            achievementBadge = BilingualText("🏅 राष्ट्रीय इंस्पायर अवार्ड विजेता (₹10,000)", "🏅 National INSPIRE Science Awardee"),
            village = BilingualText("ग्राम - गंगौली, सिमरी, बक्सर", "Vill - Gangauli, Simri, Buxar"),
            year = "2026",
            rollNo = "260105"
        )
    )

    val studentResults = listOf(
        StudentResultRecord(
            rollCode = "73045",
            rollNo = "260101",
            studentName = BilingualText("आदित्य कुमार राय (Aditya Kumar Rai)", "Aditya Kumar Rai"),
            fatherName = BilingualText("श्री मनोज कुमार राय", "Mr. Manoj Kumar Rai"),
            motherName = BilingualText("श्रीमती गायत्री देवी", "Mrs. Gayatri Devi"),
            classStream = BilingualText("कक्षा 12वीं — विज्ञान संकाय (I.Sc. PCM)", "Class 12 — Science Stream (I.Sc. PCM)"),
            examSession = "Annual Board / Institutional Assessment 2026",
            village = BilingualText("गंगौली, सिमरी, बक्सर", "Gangauli, Simri, Buxar"),
            attendancePercent = 96,
            subjects = listOf(
                SubjectMark(BilingualText("भौतिक विज्ञान (Physics)", "Physics"), 100, 66, 29),
                SubjectMark(BilingualText("रसायन विज्ञान (Chemistry)", "Chemistry"), 100, 65, 29),
                SubjectMark(BilingualText("गणित (Mathematics)", "Mathematics"), 100, 99, 0),
                SubjectMark(BilingualText("अंग्रेजी (English Rainbow)", "English"), 100, 92, 0),
                SubjectMark(BilingualText("हिंदी (दिगंत भाग-2)", "Hindi"), 100, 94, 0)
            ),
            remarks = BilingualText(
                "असाधारण प्रदर्शन! सभी विषयों में डिस्टिंक्शन (D)। NSP सेंट्रल सेक्टर छात्रवृत्ति एवं IIT-JEE हेतु अनुशंसित।",
                "Outstanding Distinction in all 5 subjects! Recommended for NSP Central Sector Scholarship."
            )
        ),
        StudentResultRecord(
            rollCode = "73045",
            rollNo = "260102",
            studentName = BilingualText("पल्लवी कुमारी पाण्डेय (Pallavi Kumari Pandey)", "Pallavi Kumari Pandey"),
            fatherName = BilingualText("श्री संजय पाण्डेय", "Mr. Sanjay Pandey"),
            motherName = BilingualText("श्रीमती किरण पाण्डेय", "Mrs. Kiran Pandey"),
            classStream = BilingualText("कक्षा 12वीं — विज्ञान संकाय (I.Sc. PCB)", "Class 12 — Science Stream (I.Sc. PCB)"),
            examSession = "Annual Board / Institutional Assessment 2026",
            village = BilingualText("गंगौली (पूरब टोला), सिमरी", "Gangauli East, Simri"),
            attendancePercent = 97,
            subjects = listOf(
                SubjectMark(BilingualText("जीव विज्ञान (Biology)", "Biology"), 100, 68, 30),
                SubjectMark(BilingualText("भौतिक विज्ञान (Physics)", "Physics"), 100, 63, 29),
                SubjectMark(BilingualText("रसायन विज्ञान (Chemistry)", "Chemistry"), 100, 64, 29),
                SubjectMark(BilingualText("अंग्रेजी (English)", "English"), 100, 91, 0),
                SubjectMark(BilingualText("हिंदी (Hindi)", "Hindi"), 100, 94, 0)
            ),
            remarks = BilingualText(
                "प्रखंड स्तर पर सर्वोच्च छात्रा अंक! मुख्यमंत्री कन्या उत्थान योजना (₹25,000) हेतु पूर्णतः पात्र।",
                "Top Female Scorer in Simri Block! Eligible for ₹25,000 Mukhyamantri Kanya Utthan Grant."
            )
        ),
        StudentResultRecord(
            rollCode = "73045",
            rollNo = "260103",
            studentName = BilingualText("प्रियांशु कुमार ओझा (Priyanshu Kumar Ojha)", "Priyanshu Kumar Ojha"),
            fatherName = BilingualText("श्री विनोद कुमार ओझा", "Mr. Vinod Kumar Ojha"),
            motherName = BilingualText("श्रीमती रीना देवी", "Mrs. Reena Devi"),
            classStream = BilingualText("कक्षा 10वीं — माध्यमिक (Matriculation)", "Class 10 — Secondary (Matriculation)"),
            examSession = "Annual Matric Assessment 2026",
            village = BilingualText("सिमरी, बक्सर", "Simri, Buxar"),
            attendancePercent = 95,
            subjects = listOf(
                SubjectMark(BilingualText("गणित (Mathematics)", "Mathematics"), 100, 98, 0),
                SubjectMark(BilingualText("विज्ञान (Science + Practical)", "Science"), 100, 78, 20),
                SubjectMark(BilingualText("सामाजिक विज्ञान (Social Science)", "Social Science"), 100, 75, 20),
                SubjectMark(BilingualText("मातृभाषा हिंदी (Hindi)", "Hindi"), 100, 93, 0),
                SubjectMark(BilingualText("संस्कृत (Sanskrit)", "Sanskrit"), 100, 92, 0)
            ),
            remarks = BilingualText(
                "विद्यालय मैट्रिक टॉपर! मुख्यमंत्री बालक प्रोत्साहन योजना (₹10,000) एवं उच्च माध्यमिक विज्ञान संकाय में सीधा प्रवेश।",
                "School Matric Topper! Qualified for ₹10,000 Mukhyamantri Merit Award and direct I.Sc. honors seat."
            )
        ),
        StudentResultRecord(
            rollCode = "73045",
            rollNo = "260104",
            studentName = BilingualText("साक्षी कुमारी चौबे (Sakshi Kumari Choubey)", "Sakshi Kumari Choubey"),
            fatherName = BilingualText("श्री दिवाकर चौबे", "Mr. Diwakar Choubey"),
            motherName = BilingualText("श्रीमती मीरा देवी", "Mrs. Meera Devi"),
            classStream = BilingualText("कक्षा 12वीं — कला संकाय (I.A. Humanities)", "Class 12 — Arts Stream (I.A. Humanities)"),
            examSession = "Annual Board Assessment 2026",
            village = BilingualText("कठार, सिमरी, बक्सर", "Kathar, Simri, Buxar"),
            attendancePercent = 94,
            subjects = listOf(
                SubjectMark(BilingualText("इतिहास (History)", "History"), 100, 96, 0),
                SubjectMark(BilingualText("राजनीति विज्ञान (Political Science)", "Political Science"), 100, 92, 0),
                SubjectMark(BilingualText("भूगोल (Geography + Practical)", "Geography"), 100, 64, 28),
                SubjectMark(BilingualText("अर्थशास्त्र (Economics)", "Economics"), 100, 88, 0),
                SubjectMark(BilingualText("हिंदी (दिगंत)", "Hindi"), 100, 89, 0)
            ),
            remarks = BilingualText(
                "कला संकाय में प्रथम स्थान! वाद-विवाद एवं निबंध लेखन में भी जिला स्तर पर पुरस्कृत।",
                "Ranked 1st in Arts & Humanities! District winner in Essay & Constitutional Debate."
            )
        ),
        StudentResultRecord(
            rollCode = "73045",
            rollNo = "260105",
            studentName = BilingualText("आयुष कुमार ठाकुर (Ayush Kumar Thakur)", "Ayush Kumar Thakur"),
            fatherName = BilingualText("श्री अरविंद ठाकुर", "Mr. Arvind Thakur"),
            motherName = BilingualText("श्रीमती पूनम देवी", "Mrs. Poonam Devi"),
            classStream = BilingualText("कक्षा 10वीं — माध्यमिक (Matriculation)", "Class 10 — Secondary (Matriculation)"),
            examSession = "Annual Matric Assessment 2026",
            village = BilingualText("गंगौली, सिमरी, बक्सर", "Gangauli, Simri, Buxar"),
            attendancePercent = 93,
            subjects = listOf(
                SubjectMark(BilingualText("गणित (Mathematics)", "Mathematics"), 100, 95, 0),
                SubjectMark(BilingualText("विज्ञान (Science)", "Science"), 100, 76, 20),
                SubjectMark(BilingualText("सामाजिक विज्ञान (Social Science)", "Social Science"), 100, 73, 19),
                SubjectMark(BilingualText("हिंदी (Hindi)", "Hindi"), 100, 91, 0),
                SubjectMark(BilingualText("संस्कृत (Sanskrit)", "Sanskrit"), 100, 90, 0)
            ),
            remarks = BilingualText(
                "उत्कृष्ट प्रदर्शन एवं अटल नवाचार क्लब में सर्वश्रेष्ठ विज्ञान मॉडल पुरस्कार विजेता।",
                "Excellent First Division with Distinction in all subjects & Best Science Model Award."
            )
        )
    )

    val noticesList = listOf(
        NoticeItem(
            id = "notice_1",
            refNo = "UMVG/ADM/2026/108",
            title = BilingualText(
                "सत्र 2026-27 हेतु कक्षा 9वीं एवं 11वीं (I.Sc., I.A., I.Com.) में नामांकन सूचना",
                "Admission Notification for Class 9th & 11th (I.Sc., I.A., I.Com.) Session 2026-27"
            ),
            category = "ADMISSION",
            categoryLabel = BilingualText("नामांकन सूचना", "Admissions"),
            date = BilingualText("08 अक्टूबर 2026", "08 Oct 2026"),
            isUrgent = true,
            summary = BilingualText(
                "ग्राम पंचायत गंगौली एवं सिमरी प्रखंड के समस्त अभिभावकों एवं छात्र-छात्राओं को सूचित किया जाता है कि उच्च माध्यमिक विद्यालय गंगौली (कोड: 73045) में नवीन सत्र हेतु नामांकन प्रक्रिया प्रारंभ है।",
                "All parents and students of Gangauli Panchayat and Simri Block are informed that admissions for Class 9 and Class 11 (+2) at UMV Gangauli (Code: 73045) are open."
            ),
            fullCircular = BilingualText(
                "कार्यालय: प्राचार्य, उच्च माध्यमिक विद्यालय गंगौली, सिमरी, बक्सर (बिहार)\nपत्रांक: UMVG/ADM/2026/108\n\nएतद् द्वारा सिमरी प्रखंड के समस्त छात्र-छात्राओं एवं अभिभावकों को सूचित किया जाता है कि कक्षा 9वीं एवं OFSS बिहार के माध्यम से कक्षा 11वीं (विज्ञान, कला एवं वाणिज्य संकाय) में नामांकन चालू है।\n\nआवश्यक कागजात:\n1. विद्यालय परित्याग प्रमाण पत्र (SLC / TC) की मूल प्रति\n2. कक्षा 8वीं / 10वीं का अंक-पत्र एवं प्रवेश पत्र\n3. आधार कार्ड एवं बैंक पासबुक की छायाप्रति (DBT हेतु)\n4. 4 नवीनतम पासपोर्ट आकार के रंगीन फोटो\n5. जाति, आय एवं निवास प्रमाण पत्र (यदि लागू हो)\n\nअभ्यर्थी इस पोर्टल के 'नामांकन (Admissions)' टैब से भी अपना ऑनलाइन टोकन प्राप्त कर सकते हैं।",
                "OFFICE OF THE PRINCIPAL\nUchch Madhyamik Vidyalaya Gangauli, Simri, Buxar (Bihar)\nRef: UMVG/ADM/2026/108\n\nAdmissions for Class 9th and Class 11th (Science, Arts & Commerce via OFSS Bihar) are open.\n\nRequired Documents:\n1. Original School Leaving Certificate (SLC / TC)\n2. Marks Sheet & Admit Card of Qualifying Exam\n3. Aadhaar Card & Aadhaar-seeded Bank Passbook copy for DBT Scholarships\n4. 4 Recent Passport Size Photographs\n5. Caste / Income / Domicile Certificate (if applicable)\n\nStudents can generate their Instant Provisional Admission Token directly from the 'Admissions' tab of this portal."
            ),
            issuedBy = BilingualText("डॉ. राकेश कुमार पाण्डेय (प्राचार्य)", "Dr. Rakesh Kumar Pandey (Principal)")
        ),
        NoticeItem(
            id = "notice_2",
            refNo = "UMVG/SCH/2026/114",
            title = BilingualText(
                "मेधासॉफ्ट (MedhaSoft) छात्रवृत्ति, साइकिल, पोशाक एवं कन्या उत्थान ई-केवाईसी शिविर",
                "MedhaSoft Scholarship, Cycle, Dress & Kanya Utthan e-KYC Verification Camp"
            ),
            category = "SCHOLARSHIP",
            categoryLabel = BilingualText("छात्रवृत्ति एवं DBT", "Scholarships"),
            date = BilingualText("05 अक्टूबर 2026", "05 Oct 2026"),
            isUrgent = true,
            summary = BilingualText(
                "75% उपस्थिति पूर्ण करने वाले सभी छात्र-छात्राओं के बैंक खाते में डीबीटी (DBT) राशि अंतरण एवं मुख्यमंत्री बालक/बालिका प्रोत्साहन योजना का सत्यापन ICT लैब में किया जा रहा है।",
                "DBT benefit verification for students fulfilling the 75% attendance norm and Mukhyamantri Kanya Utthan (₹25,000) / Matric Protsahan (₹10,000) is underway in the ICT Lab."
            ),
            fullCircular = BilingualText(
                "सूचना: मेधासॉफ्ट एवं ई-कल्याण छात्रवृत्ति प्रकोष्ठ\n\nविद्यालय के कक्षा 9वीं से 12वीं के सभी छात्र-छात्राओं को निर्देशित किया जाता है कि मुख्यमंत्री साइकिल योजना (₹3,000), मुख्यमंत्री पोशाक योजना, किशोरी स्वास्थ्य योजना एवं बोर्ड उत्तीर्ण विद्यार्थियों हेतु ₹10,000 / ₹25,000 प्रोत्साहन राशि के लिए अपना आधार सीडेड बैंक खाता विवरण ICT कंप्यूटर लैब में जमा करें।\n\nविशेष नोट: बिहार शिक्षा विभाग के नियमानुसार 75% उपस्थिति अनिवार्य है।",
                "NOTICE: MedhaSoft & e-Kalyan Scholarship Cell\n\nAll students of Class 9 to 12 are advised to verify their Aadhaar-seeded bank account details at the school ICT Computer Lab for smooth disbursement of Mukhyamantri Cycle Yojana (₹3,000), Uniform Grant, and Board Merit Incentive (₹10,000 for Matric 1st Div / ₹25,000 for Inter Girls).\n\nNote: Minimum 75% class attendance is strictly mandatory as per Bihar Education Department norms."
            ),
            issuedBy = BilingualText("छात्रवृत्ति एवं मेधासॉफ्ट नोडल पदाधिकारी", "Nodal Officer, MedhaSoft Scholarship Cell")
        ),
        NoticeItem(
            id = "notice_3",
            refNo = "UMVG/EXM/2026/119",
            title = BilingualText(
                "मासिक मूल्यांकन एवं इंटर/मैट्रिक सेंट-अप (Sent-up) परीक्षा समय-सारणी जारी",
                "Monthly Assessment & Matric/Inter Sent-up Examination Schedule Released"
            ),
            category = "EXAM",
            categoryLabel = BilingualText("परीक्षा विभाग", "Examinations"),
            date = BilingualText("02 अक्टूबर 2026", "02 Oct 2026"),
            isUrgent = false,
            summary = BilingualText(
                "बिहार विद्यालय परीक्षा समिति (BSEB) पटना के निर्देशानुसार मासिक परीक्षा एवं बोर्ड सेंट-अप प्रायोगिक/सैद्धांतिक परीक्षा की तिथि घोषित कर दी गई है।",
                "As per Bihar School Examination Board (BSEB) Patna directives, the schedule for monthly tests and Sent-up Practical/Theory exams has been published."
            ),
            fullCircular = BilingualText(
                "परीक्षा नियंत्रक कार्यालय, उच्च माध्यमिक विद्यालय गंगौली\n\n1. प्रथम पाली: प्रातः 09:30 से 12:45 बजे (विज्ञान एवं मातृभाषा)\n2. द्वितीय पाली: अपराह्न 02:00 से 05:15 बजे (गणित एवं सामाजिक विज्ञान/मानविकी)\n3. प्रायोगिक परीक्षा (Practical Exam): विद्यालय की भौतिकी, रसायन एवं जीव विज्ञान प्रयोगशाला में संपन्न होगी।\n\nसभी परीक्षार्थी विद्यालय के निर्धारित गणवेश (Uniform) एवं परिचय पत्र के साथ उपस्थित हों।",
                "OFFICE OF THE EXAMINATION CONTROLLER\n\n1. First Sitting: 09:30 AM – 12:45 PM\n2. Second Sitting: 02:00 PM – 05:15 PM\n3. Science & Geography Practicals will be conducted in the respective school laboratories.\n\nAll students must attend in full school uniform with their School ID Card."
            ),
            issuedBy = BilingualText("डॉ. राजीव नयन मिश्रा (परीक्षा नियंत्रक)", "Dr. Rajeev Nayan Mishra (Exam Controller)")
        ),
        NoticeItem(
            id = "notice_4",
            refNo = "UMVG/CUL/2026/122",
            title = BilingualText(
                "वार्षिक खेलकूद 'मशाल एवं तरंग' तथा जिला स्तरीय विज्ञान प्रदर्शनी में पंजीकरण",
                "Registration Open for Annual 'Mashal & Tarang' Sports Meet and Science Exhibition"
            ),
            category = "CULTURAL",
            categoryLabel = BilingualText("खेल एवं संस्कृति", "Sports & Events"),
            date = BilingualText("28 सितंबर 2026", "28 Sep 2026"),
            isUrgent = false,
            summary = BilingualText(
                "एथलेटिक्स, कबड्डी, वॉलीबॉल, क्रिकेट, पेंटिंग, भाषण एवं अटल विज्ञान मॉडल प्रदर्शनी में भाग लेने के इच्छुक विद्यार्थी अपने वर्ग शिक्षक से संपर्क करें।",
                "Students interested in Athletics, Kabaddi, Volleyball, Cricket, Classical Music, Debate, and Science Model Exhibition may register with their Class Teacher."
            ),
            fullCircular = BilingualText(
                "वीर कुंवर सिंह खेल परिसर एवं अटल नवाचार क्लब\n\nउच्च माध्यमिक विद्यालय गंगौली के प्रतिभाशाली खिलाड़ियों एवं युवा वैज्ञानिकों को प्रखंड एवं बक्सर जिला स्तरीय प्रतियोगिता में प्रतिनिधित्व करने हेतु चयन ट्रायल आगामी शनिवार को आयोजित होगा।\n\nश्रेणियां:\n• अंडर-14 एवं अंडर-17 (कक्षा 9-10)\n• अंडर-19 सीनियर वर्ग (कक्षा 11-12)",
                "VEER KUNWAR SINGH SPORTS & INNOVATION CELL\n\nSelection trials for representing UMV Gangauli at the Simri Block and Buxar District Championships will be held this Saturday.\n\nCategories:\n• Under-14 & Under-17 (Class 9–10)\n• Under-19 Senior Division (Class 11–12)"
            ),
            issuedBy = BilingualText("श्री विक्रमादित्य सिंह (खेल एवं सांस्कृतिक प्रभारी)", "Mr. Vikramaditya Singh (Sports & Cultural Coordinator)")
        )
    )

    val scholarshipSchemes = listOf(
        ScholarshipScheme(
            id = "sch_1",
            schemeName = BilingualText(
                "मुख्यमंत्री कन्या उत्थान योजना (इंटरमीडिएट उत्तीर्ण छात्रा प्रोत्साहन)",
                "Mukhyamantri Kanya Utthan Yojana (+2 Inter Girls Merit Grant)"
            ),
            amountBadge = BilingualText("₹25,000 एकमुश्त अनुदान", "₹25,000 Direct Bank Grant"),
            targetClasses = listOf("12"),
            targetGender = "FEMALE",
            minPercentage = 30,
            description = BilingualText(
                "बिहार विद्यालय परीक्षा समिति से कक्षा 12वीं (इंटरमीडिएट) किसी भी श्रेणी (1st/2nd/3rd) से उत्तीर्ण करने वाली सभी अविवाहित छात्राओं को उच्च शिक्षा हेतु ₹25,000 की प्रोत्साहन राशि सीधे बैंक खाते में प्रदान की जाती है।",
                "Every unmarried girl student passing Class 12 (Intermediate) from BSEB in any division receives ₹25,000 directly via DBT to pursue higher education."
            ),
            requiredDocs = listOf(
                BilingualText("इंटरमीडिएट अंक-पत्र एवं प्रवेश पत्र", "Class 12 Marks Sheet & Admit Card"),
                BilingualText("आधार कार्ड एवं आधार-सीडेड बैंक पासबुक", "Aadhaar Card & Aadhaar-Seeded Bank Account"),
                BilingualText("अविवाहित होने का स्व-घोषणा पत्र एवं मोबाइल नंबर", "Unmarried Declaration & Active Mobile Number")
            ),
            portalName = "MedhaSoft Bihar (medhasoft.bih.nic.in)"
        ),
        ScholarshipScheme(
            id = "sch_2",
            schemeName = BilingualText(
                "मुख्यमंत्री बालक/बालिका मैट्रिक प्रोत्साहन योजना (10वीं प्रथम श्रेणी)",
                "Mukhyamantri Matric Protsahan Yojana (Class 10 First Division)"
            ),
            amountBadge = BilingualText("₹10,000 प्रोत्साहन राशि", "₹10,000 Merit Award"),
            targetClasses = listOf("10"),
            targetGender = "ALL",
            minPercentage = 60,
            description = BilingualText(
                "कक्षा 10वीं (मैट्रिक बोर्ड परीक्षा) में प्रथम श्रेणी (60% या अधिक अंक) प्राप्त करने वाले सभी छात्र-छात्राओं एवं SC/ST श्रेणी के द्वितीय श्रेणी उत्तीर्ण विद्यार्थियों को ₹10,000/₹8,000 की प्रोत्साहन राशि।",
                "Students securing 1st Division (60%+) in BSEB Class 10 Matric exams receive ₹10,000 directly into their bank account (₹8,000 for SC/ST 2nd Division)."
            ),
            requiredDocs = listOf(
                BilingualText("मैट्रिक अंक-पत्र एवं पंजीयन संख्या", "Class 10 Marks Sheet & Registration No."),
                BilingualText("आधार कार्ड, आय प्रमाण पत्र एवं बैंक पासबुक", "Aadhaar Card, Income Certificate & Bank Passbook")
            ),
            portalName = "MedhaSoft Bihar Portal"
        ),
        ScholarshipScheme(
            id = "sch_3",
            schemeName = BilingualText(
                "मुख्यमंत्री बालिका/बालक साइकिल योजना (कक्षा 9वीं)",
                "Mukhyamantri Cycle Yojana (Class 9th Mobility Grant)"
            ),
            amountBadge = BilingualText("₹3,000 साइकिल अनुदान", "₹3,000 Cycle Grant"),
            targetClasses = listOf("9"),
            targetGender = "ALL",
            minPercentage = 0,
            description = BilingualText(
                "कक्षा 9वीं में नामांकित एवं न्यूनतम 75% उपस्थिति दर्ज कराने वाले सभी छात्र-छात्राओं को विद्यालय आवागमन की सुविधा हेतु ₹3,000 की राशि डीबीटी (DBT) के माध्यम से।",
                "All Class 9 students maintaining 75%+ attendance receive ₹3,000 via Direct Benefit Transfer to purchase a bicycle for easy commute from nearby villages."
            ),
            requiredDocs = listOf(
                BilingualText("कक्षा 9वीं में नामांकन एवं 75% उपस्थिति प्रमाण", "Class 9 Enrollment & 75% Attendance Verification"),
                BilingualText("छात्र/छात्रा के नाम से बैंक खाता एवं आधार कार्ड", "Student Bank Passbook & Aadhaar Card")
            ),
            portalName = "MedhaSoft Attendance DBT"
        ),
        ScholarshipScheme(
            id = "sch_4",
            schemeName = BilingualText(
                "मुख्यमंत्री पोशाक, छात्रवृत्ति एवं किशोरी स्वास्थ्य योजना (कक्षा 9 से 12)",
                "Mukhyamantri Uniform, Stipend & Kishori Swasthya Grant (Class 9–12)"
            ),
            amountBadge = BilingualText("₹1,500 – ₹3,600 वार्षिक", "₹1,500 – ₹3,600 Annual"),
            targetClasses = listOf("9", "10", "11", "12"),
            targetGender = "ALL",
            minPercentage = 0,
            description = BilingualText(
                "कक्षा 9वीं से 12वीं के सभी विद्यार्थियों को पोशाक राशि (₹1,500), वार्षिक छात्रवृत्ति (₹1,800) एवं छात्राओं को किशोरी स्वास्थ्य योजना (₹300) तथा निःशुल्क FLN/पाठ्यपुस्तक किट।",
                "Annual DBT grant covering School Uniform (₹1,500), Annual Stipend (₹1,800), Kishori Health Hygiene grant (₹300 for girls), and free Student Learning Kit."
            ),
            requiredDocs = listOf(
                BilingualText("विद्यालय उपस्थिति पंजी में 75% उपस्थिति", "75% Attendance in School Register"),
                BilingualText("मेधासॉफ्ट पोर्टल पर ई-केवाईसी", "MedhaSoft Portal e-KYC Verification")
            ),
            portalName = "Bihar Education Dept DBT"
        ),
        ScholarshipScheme(
            id = "sch_5",
            schemeName = BilingualText(
                "बिहार स्टूडेंट क्रेडिट कार्ड एवं पोस्ट-मैट्रिक छात्रवृत्ति (PMS)",
                "Bihar Student Credit Card (₹4 Lakh) & Post-Matric Scholarship"
            ),
            amountBadge = BilingualText("₹4,00,000 तक उच्च शिक्षा ऋण", "Up to ₹4,00,000 Higher Ed Support"),
            targetClasses = listOf("11", "12"),
            targetGender = "ALL",
            minPercentage = 45,
            description = BilingualText(
                "12वीं उत्तीर्ण होने के पश्चात B.Tech, MBBS, B.Sc, BBA, BCA, नर्सिंग या पॉलिटेक्निक सहित 42+ उच्च शिक्षा पाठ्यक्रमों के लिए मात्र 1% (छात्रा/दिव्यांग) या 4% साधारण ब्याज पर ₹4 लाख तक की सहायता।",
                "Provides up to ₹4 Lakh support for pursuing B.Tech, Medical, Nursing, B.Sc, BBA, or Polytechnic after Class 12 at nominal 1% interest for girls."
            ),
            requiredDocs = listOf(
                BilingualText("10वीं एवं 12वीं प्रमाण पत्र, निवास प्रमाण पत्र", "10th & 12th Certificates & Bihar Residence Proof"),
                BilingualText("उच्च शिक्षण संस्थान का बोनाफाइड एवं शुल्क संरचना", "Higher Institute Admission Proof & Fee Structure")
            ),
            portalName = "MNSSBY (7nishchay-yuvaupmission.bihar.gov.in)"
        )
    )

    val studyMaterials = listOf(
        StudyMaterial(
            id = "mat_1",
            title = BilingualText(
                "भौतिकी (Physics): प्रकाशिकी एवं स्थिरवैद्युतिकी — संपूर्ण सूत्र एवं बोर्ड नोट्स",
                "Physics: Ray Optics & Electrostatics — Formula Sheet & Board Notes"
            ),
            subject = BilingualText("भौतिक विज्ञान (Physics)", "Physics"),
            classLevel = "Class 11-12 Science",
            materialType = BilingualText("ई-नोट्स एवं 50 वस्तुनिष्ठ प्रश्न (OMR)", "Revision Notes + 50 MCQs"),
            chaptersOrPages = BilingualText("24 पृष्ठ • डॉ. राजीव नयन मिश्रा द्वारा तैयार", "24 Pages • Prepared by Dr. Rajeev Nayan Mishra"),
            keyTopics = listOf(
                BilingualText("गॉस का प्रमेय (Gauss's Law) एवं इसके अनुप्रयोग", "Gauss's Theorem & Electric Flux Derivations"),
                BilingualText("लेंस मेकर सूत्र एवं पूर्ण आंतरिक परावर्तन (TIR)", "Lens Maker's Formula & Total Internal Reflection"),
                BilingualText("खगोलीय दूरदर्शी एवं संयुक्त सूक्ष्मदर्शी की आवर्धन क्षमता", "Compound Microscope & Astronomical Telescope Ray Diagrams")
            ),
            summaryNotes = BilingualText(
                "महत्वपूर्ण सूत्र सारांश:\n1. कूलॉम का नियम: F = (1 / 4πε₀) · (q₁q₂ / r²)\n2. गॉस का प्रमेय: Φ = ∮ E · dA = q_in / ε₀\n3. लेंस मेकर सूत्र: 1/f = (μ - 1)(1/R₁ - 1/R₂)\n4. प्रिज्म अपवर्तनांक: μ = sin((A + δm)/2) / sin(A/2)\n\nबोर्ड परीक्षा टिप: दीर्घ उत्तरीय प्रश्नों में स्वच्छ नामांकित किरण आरेख (Ray Diagram) पेंसिल से अवश्य बनाएं।",
                "KEY FORMULAS & BOARD TIPS:\n1. Coulomb's Law: F = (1 / 4πε₀) · (q₁q₂ / r²)\n2. Gauss's Law: Φ = ∮ E · dA = q_in / ε₀\n3. Lens Maker's Formula: 1/f = (μ - 1)(1/R₁ - 1/R₂)\n4. Prism Formula: μ = sin((A + δm)/2) / sin(A/2)\n\nBoard Exam Tip: Always draw neat, labeled ray diagrams in 5-mark long-answer questions."
            )
        ),
        StudyMaterial(
            id = "mat_2",
            title = BilingualText(
                "मैट्रिक गणित (Class 10): त्रिकोणमिति, क्षेत्रमिति एवं सांख्यिकी मास्टर गाइड",
                "Class 10 Mathematics: Trigonometry, Mensuration & Statistics Master Guide"
            ),
            subject = BilingualText("गणित (Mathematics)", "Mathematics"),
            classLevel = "Class 9-10",
            materialType = BilingualText("मॉडल प्रश्न बैंक एवं शॉर्टकट ट्रिक्स", "Model Question Bank & Formula Guide"),
            chaptersOrPages = BilingualText("18 पृष्ठ • श्री सत्येंद्र नारायण सिंह", "18 Pages • Mr. Satyendra Narayan Singh"),
            keyTopics = listOf(
                BilingualText("त्रिकोणमितीय सर्वसमिकाएं एवं ऊंचाई-दूरी (Height & Distance)", "Trigonometric Identities & Heights and Distances"),
                BilingualText("शंकु, बेलन, गोला एवं छिन्नक (Frustum) का पृष्ठीय क्षेत्रफल", "Surface Area & Volume of Cone, Sphere & Frustum"),
                BilingualText("माध्य, माध्यिका एवं बहुलक (Mean, Median, Mode) संबंध", "Empirical Formula: Mode = 3 Median - 2 Mean")
            ),
            summaryNotes = BilingualText(
                "सर्वसमिकाएं:\n• sin²θ + cos²θ = 1\n• sec²θ - tan²θ = 1\n• cosec²θ - cot²θ = 1\n\nसांख्यिकी सूत्र:\n• बहुलक (Mode) = 3 × माध्यिका (Median) - 2 × माध्य (Mean)\n• समांतर श्रेणी (AP) का nवां पद: aₙ = a + (n - 1)d",
                "CORE IDENTITIES:\n• sin²θ + cos²θ = 1\n• sec²θ - tan²θ = 1\n• cosec²θ - cot²θ = 1\n\nSTATISTICS & AP:\n• Mode = 3 × Median - 2 × Mean\n• nth Term of AP: aₙ = a + (n - 1)d"
            )
        ),
        StudyMaterial(
            id = "mat_3",
            title = BilingualText(
                "इतिहास एवं नागरिक शास्त्र: बक्सर का ऐतिहासिक महत्व एवं भारतीय राष्ट्रीय आंदोलन",
                "History & Polity: Heritage of Buxar, 1857 Revolution & Indian Constitution"
            ),
            subject = BilingualText("इतिहास एवं राजनीति विज्ञान", "History & Political Science"),
            classLevel = "Class 11-12 Arts",
            materialType = BilingualText("अध्यायवार सार एवं BPSC/CUET विशेष नोट्स", "Chapter Notes & CUET/BPSC Compendium"),
            chaptersOrPages = BilingualText("22 पृष्ठ • डॉ. अरुण कुमार चौबे", "22 Pages • Dr. Arun Kumar Choubey"),
            keyTopics = listOf(
                BilingualText("1764 का बक्सर युद्ध एवं इलाहाबाद की संधि का ऐतिहासिक प्रभाव", "Battle of Buxar (1764) & Treaty of Allahabad"),
                BilingualText("1857 की क्रांति में बाबू वीर कुंवर सिंह का अद्वितीय योगदान", "Role of Babu Veer Kunwar Singh in the 1857 War of Independence"),
                BilingualText("भारतीय संविधान के मौलिक अधिकार (अनुच्छेद 12-35) एवं नीति निदेशक तत्व", "Fundamental Rights (Articles 12–35) & Directive Principles")
            ),
            summaryNotes = BilingualText(
                "बक्सर एवं बिहार गौरव सार:\n• अक्टूबर 1764: बक्सर के युद्ध ने भारत के आधुनिक राजनीतिक इतिहास की दिशा बदली।\n• 1857-58: 80 वर्ष की आयु में बाबू वीर कुंवर सिंह ने जगदीशपुर से आजमगढ़ तक अदम्य शौर्य का परिचय दिया।\n• संविधान सभा के प्रथम स्थायी अध्यक्ष: देशरत्न डॉ. राजेंद्र प्रसाद (11 दिसंबर 1946)।",
                "HISTORICAL HIGHLIGHTS:\n• Oct 1764: The Battle of Buxar proved a pivotal turning point in modern Indian history.\n• 1857–58: Babu Veer Kunwar Singh led a legendary campaign across Shahabad, Ganga belt, and Azamgarh.\n• Dr. Rajendra Prasad of Bihar served as the first Permanent President of the Constituent Assembly."
            )
        ),
        StudyMaterial(
            id = "mat_4",
            title = BilingualText(
                "कंप्यूटर साइंस एवं AI: पाइथन (Python), इंटरनेट सुरक्षा एवं डिजिटल बिहार",
                "Computer Science & AI: Python Basics, Cyber Safety & Digital Skills"
            ),
            subject = BilingualText("कंप्यूटर एवं सूचना प्रौद्योगिकी", "Computer Science & ICT"),
            classLevel = "Class 9-10",
            materialType = BilingualText("प्रैक्टिकल लैब मैनुअल", "Practical Lab Handbook"),
            chaptersOrPages = BilingualText("16 पृष्ठ • श्री आलोक रंजन तिवारी", "16 Pages • Mr. Alok Ranjan Tiwari"),
            keyTopics = listOf(
                BilingualText("Python में Variables, Loops और Functions का प्रयोग", "Variables, Loops, and Functions in Python"),
                BilingualText("साइबर सुरक्षा, OTP धोखाधड़ी से बचाव एवं डिजीलॉकर (DigiLocker)", "Cyber Hygiene, UPI/OTP Safety & Using DigiLocker for Board Certificates"),
                BilingualText("HTML5 एवं स्प्रेडशीट डेटा विश्लेषण", "HTML5 Web Structuring & Spreadsheet Data Analysis")
            ),
            summaryNotes = BilingualText(
                "डिजिटल टिप:\nसभी विद्यार्थी अपने मैट्रिक एवं इंटरमीडिएट के मूल प्रमाण पत्र भारत सरकार के 'DigiLocker' ऐप पर सुरक्षित रूप से प्राप्त कर सकते हैं। ICT लैब में प्रत्येक विद्यार्थी को इसका निःशुल्क प्रशिक्षण दिया जाता है।",
                "DIGITAL TIP:\nStudents can securely access verified BSEB Marksheets and Certificates on DigiLocker. Hands-on Python and DigiLocker training is conducted weekly in the UMV Gangauli ICT Lab."
            )
        )
    )

    val homeworkList = listOf(
        HomeworkItem(
            id = "hw_1",
            classLevel = "Class 10",
            subject = BilingualText("गणित (Mathematics)", "Mathematics"),
            title = BilingualText("प्रश्नावली 8.4 (त्रिकोणमितीय सर्वसमिकाएं) के प्रश्न 1 से 5 हल करें", "Solve Exercise 8.4 (Trigonometric Identities) Q1 to Q5"),
            description = BilingualText("प्रत्येक चरण में प्रयुक्त सूत्र को स्पष्ट रूप से दाईं ओर लिखें।", "Write the trigonometric identity used alongside each step clearly."),
            assignedBy = BilingualText("श्री सत्येंद्र नारायण सिंह", "Mr. Satyendra Narayan Singh"),
            dueDate = BilingualText("कल प्रातः 10:15 बजे", "Tomorrow, 10:15 AM")
        ),
        HomeworkItem(
            id = "hw_2",
            classLevel = "Class 12 Science",
            subject = BilingualText("भौतिक विज्ञान (Physics)", "Physics"),
            title = BilingualText("संयुक्त सूक्ष्मदर्शी (Compound Microscope) का किरण आरेख एवं आवर्धन व्यंजक", "Draw Ray Diagram of Compound Microscope & derive Magnifying Power"),
            description = BilingualText("प्रायोगिक कॉपी में प्रयोग संख्या-4 का प्रेक्षण सारणी भी पूर्ण करें।", "Also complete Observation Table for Experiment-4 in Practical Record Book."),
            assignedBy = BilingualText("डॉ. राजीव नयन मिश्रा", "Dr. Rajeev Nayan Mishra"),
            dueDate = BilingualText("शनिवार, 11:00 बजे", "Saturday, 11:00 AM")
        ),
        HomeworkItem(
            id = "hw_3",
            classLevel = "Class 12 Arts",
            subject = BilingualText("इतिहास एवं राजनीति विज्ञान", "History & Polity"),
            title = BilingualText("'1857 के स्वतंत्रता संग्राम में बिहार एवं शाहाबाद का योगदान' पर 300 शब्दों में लेख", "Write a 300-word analytical note on 'Bihar & Shahabad in the 1857 Uprising'"),
            description = BilingualText("मानचित्र में जगदीशपुर, बक्सर, आरा एवं पटना के प्रमुख केंद्रों को चिह्नित करें।", "Mark Jagdishpur, Buxar, Arrah, and Patna on the outline map of Bihar."),
            assignedBy = BilingualText("डॉ. अरुण कुमार चौबे", "Dr. Arun Kumar Choubey"),
            dueDate = BilingualText("सोमवार, 10:30 बजे", "Monday, 10:30 AM")
        ),
        HomeworkItem(
            id = "hw_4",
            classLevel = "Class 9",
            subject = BilingualText("अंग्रेजी एवं ICT (English & ICT)", "English & ICT"),
            title = BilingualText("'My Ideal Village Gangauli' विषय पर 10 वाक्यों का अनुच्छेद एवं MS Word अभ्यास", "Write a 10-sentence paragraph on 'My Village Gangauli' & format in Word"),
            description = BilingualText("ICT लैब सत्र में इसे टाइप कर प्रिंट प्रिव्यू शिक्षक को दिखाएं।", "Type and format your paragraph during your scheduled ICT Lab period."),
            assignedBy = BilingualText("श्रीमती नेहा सिन्हा एवं श्री आलोक तिवारी", "Mrs. Neha Sinha & Mr. Alok Tiwari"),
            dueDate = BilingualText("शुक्रवार, 01:15 बजे", "Friday, 01:15 PM")
        )
    )

    val academicCalendarEvents = listOf(
        AcademicEvent(
            id = "ev_1",
            monthBadge = "OCT",
            dayNum = "15",
            title = BilingualText("डॉ. ए.पी.जे. अब्दुल कलाम जयंती — 'विश्व छात्र दिवस' एवं विज्ञान प्रदर्शनी", "Dr. A.P.J. Abdul Kalam Jayanti — World Students' Day & Science Fair"),
            subtitle = BilingualText("अटल टिंकरिंग एवं विज्ञान प्रयोगशाला में छात्र नवाचार मॉडल प्रस्तुति", "Student Robotics & Green Energy Model Showcase at Main Hall"),
            categoryBadge = BilingualText("विज्ञान उत्सव", "Science Fest")
        ),
        AcademicEvent(
            id = "ev_2",
            monthBadge = "OCT",
            dayNum = "24",
            title = BilingualText("अभिभावक-शिक्षक संगोष्ठी (PTM) एवं प्रगति पत्रक वितरण", "Mega Parent-Teacher Meet (PTM) & Progress Card Review"),
            subtitle = BilingualText("गंगौली एवं सिमरी क्षेत्र के सभी अभिभावकों के साथ शैक्षणिक संवाद", "Interactive consultation with parents of Class 9 to 12 students"),
            categoryBadge = BilingualText("अभिभावक संवाद", "Parent Meet")
        ),
        AcademicEvent(
            id = "ev_3",
            monthBadge = "NOV",
            dayNum = "14",
            title = BilingualText("वार्षिक खेलकूद महोत्सव 'तरंग एवं मशाल' — सिमरी प्रखंड चैंपियनशिप", "Annual Sports Championship 'Tarang & Mashal' — Simri Block"),
            subtitle = BilingualText("100m दौड़, कबड्डी, वॉलीबॉल, क्रिकेट एवं छात्राओं की साइकिल रैली", "Track & Field, Kabaddi, Volleyball, Cricket & Girls' Cycle Rally"),
            categoryBadge = BilingualText("खेल महोत्सव", "Sports Meet")
        ),
        AcademicEvent(
            id = "ev_4",
            monthBadge = "NOV",
            dayNum = "26",
            title = BilingualText("संविधान दिवस एवं अंतर-सदन भाषण प्रतियोगिता", "Constitution Day (Samvidhan Diwas) & Inter-House Debate"),
            subtitle = BilingualText("मौलिक अधिकार एवं कर्तव्य विषय पर प्रश्नोत्तरी एवं निबंध", "Constitutional Quiz, Preamble Oath & Youth Parliament"),
            categoryBadge = BilingualText("सांस्कृतिक", "Cultural")
        ),
        AcademicEvent(
            id = "ev_5",
            monthBadge = "DEC",
            dayNum = "03",
            title = BilingualText("देशरत्न डॉ. राजेंद्र प्रसाद जयंती — 'मेधा दिवस' सम्मान समारोह", "Dr. Rajendra Prasad Jayanti — Bihar 'Medha Diwas' Merit Felicitation"),
            subtitle = BilingualText("विद्यालय के सर्वोच्च अंक प्राप्त करने वाले मेधावी विद्यार्थियों का सम्मान", "Felicitation of Board Toppers & 100% Attendance Achievers"),
            categoryBadge = BilingualText("मेधा सम्मान", "Merit Day")
        )
    )

    val admissionDocChecklist = listOf(
        Pair("doc_1", BilingualText("विद्यालय परित्याग प्रमाण पत्र (SLC / TC) की मूल प्रति", "Original School Leaving Certificate (SLC / TC)")),
        Pair("doc_2", BilingualText("पिछली कक्षा (8वीं / 10वीं) का अंक-पत्र एवं प्रवेश पत्र", "Previous Class (8th / 10th) Marksheet & Admit Card")),
        Pair("doc_3", BilingualText("विद्यार्थी का आधार कार्ड (Aadhaar Card) की छायाप्रति", "Copy of Student's Aadhaar Card")),
        Pair("doc_4", BilingualText("आधार से लिंक बैंक पासबुक (DBT छात्रवृत्ति एवं साइकिल/पोशाक हेतु)", "Aadhaar-seeded Bank Passbook (for DBT Scholarships)")),
        Pair("doc_5", BilingualText("4 नवीनतम पासपोर्ट आकार के रंगीन फोटो (विद्यालय ड्रेस में)", "4 Recent Passport-size Color Photographs")),
        Pair("doc_6", BilingualText("जाति, आय एवं निवास प्रमाण पत्र (आरक्षण एवं छात्रवृत्ति लाभ हेतु)", "Caste, Income & Domicile Certificate (for Scholarship/Reservation)")),
        Pair("doc_7", BilingualText("OFSS बिहार इंटीमेशन लेटर (केवल कक्षा 11वीं नामांकन हेतु)", "OFSS Bihar Intimation Letter (for Class 11th Admission only)"))
    )

    val simriBlockVillages = listOf(
        "Gangauli (गंगौली)",
        "Simri (सिमरी)",
        "Kathar (कठार)",
        "Dumri (डुमरी)",
        "Rajpur (राजपुर)",
        "Balihar (बलिहार)",
        "Gaighat (गायघाट)",
        "Chakni (चकनी)",
        "Dullahpur (दुल्लहपुर)",
        "Niyazipur (नियाजीपुर)",
        "Keshopur (केशोपुर)",
        "Buxar / Other (अन्य)"
    )
}
