package com.example.data

import android.content.Context
import org.json.JSONArray

object DeviceCatalog {
    val totalUniqueCount = 131
    val totalActivations = 2007
    val totalIphones = 56
    val totalIpads = 72
    val totalIpods = 3
    val lastUpdateDate = "2026-09-30"

    fun loadAdditionalDevices(context: Context): List<SupportedDevice> {
        val catalogJson = context.assets.open("supportedDevices.json")
            .bufferedReader()
            .use { it.readText() }
        val entries = JSONArray(catalogJson)
        val knownIdentifiers = devices.mapTo(mutableSetOf()) { it.identifier }

        return buildList {
            for (index in 0 until entries.length()) {
                val entry = entries.getJSONObject(index)
                val identifier = entry.getString("identifier")
                if (!knownIdentifiers.add(identifier)) continue

                val category = when (entry.getString("category")) {
                    "iphone" -> DeviceType.IPHONE
                    "ipad" -> DeviceType.IPAD
                    else -> DeviceType.IPOD
                }
                val hardwareCodesJson = entry.getJSONArray("hardwareCodes")
                val hardwareCodes = List(hardwareCodesJson.length()) { codeIndex ->
                    hardwareCodesJson.getString(codeIndex)
                }
                val iosVersionsJson = entry.getJSONArray("iosVersions")
                val iosVersions = List(iosVersionsJson.length()) { versionIndex ->
                    val version = iosVersionsJson.getJSONObject(versionIndex)
                    val buildsJson = version.getJSONArray("builds")
                    IosVersionGroup(
                        major = version.getString("major"),
                        builds = List(buildsJson.length()) { buildIndex ->
                            buildsJson.getString(buildIndex)
                        }
                    )
                }

                add(
                    SupportedDevice(
                        id = entry.getString("id"),
                        name = entry.getString("name"),
                        identifier = identifier,
                        isNew = entry.optBoolean("isNew", false),
                        hardwareCodes = hardwareCodes,
                        category = category,
                        iosVersions = iosVersions
                    )
                )
            }
        }
    }

    val devices: List<SupportedDevice> = listOf(
        // iPhones
        SupportedDevice(
            id = "ip7p_94",
            name = "iPhone 7 Plus",
            identifier = "iPhone9,4",
            isNew = true,
            hardwareCodes = listOf("A1784"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 12", listOf("12.1.4 (16D57)")),
                IosVersionGroup("iOS 13", listOf("13.5.1 (17F80)")),
                IosVersionGroup("iOS 14", listOf("14.4.2 (18D70)", "14.7.1 (18G82)")),
                IosVersionGroup("iOS 15", listOf("15.0", "15.1", "15.4.1", "15.6.1", "15.7.8", "15.8.4", "15.8.8 (19H422)")),
                IosVersionGroup("iOS 16", listOf("16.1 (20B82)"))
            )
        ),
        SupportedDevice(
            id = "ip11_121",
            name = "iPhone 11",
            identifier = "iPhone12,1",
            isNew = true,
            hardwareCodes = listOf("A2111", "A2223", "A2221", "A2222"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 13", listOf("13.4.1")),
                IosVersionGroup("iOS 14", listOf("14.0.1", "14.3", "14.4.2", "14.7.1", "14.8.1")),
                IosVersionGroup("iOS 15", listOf("15.0.2", "15.1", "15.3.1", "15.4.1", "15.6.1")),
                IosVersionGroup("iOS 16", listOf("16.0", "16.1", "16.2", "16.3.1", "16.5.1", "16.6.1")),
                IosVersionGroup("iOS 17", listOf("17.0.3", "17.1.2", "17.2.1", "17.3.1", "17.4.1", "17.5.1", "17.6.1", "17.7.2")),
                IosVersionGroup("iOS 18", listOf("18.0", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A340)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip6p_71",
            name = "iPhone 6 Plus",
            identifier = "iPhone7,1",
            isNew = false,
            hardwareCodes = listOf("A1522", "A1524", "A1593"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 10", listOf("10.2 (14C92)", "10.2.1 (14D27)")),
                IosVersionGroup("iOS 12", listOf("12.1.3", "12.4.8", "12.5.5", "12.5.7", "12.5.8 (16H88)")),
                IosVersionGroup("iOS 15", listOf("15.8.6 (19H402)")),
                IosVersionGroup("iOS 17", listOf("17.5 (21F5063f)"))
            )
        ),
        SupportedDevice(
            id = "ipxr_118",
            name = "iPhone XR",
            identifier = "iPhone11,8",
            isNew = true,
            hardwareCodes = listOf("A1984", "A2105", "A2106", "A2107"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 13", listOf("13.4.1", "13.5.1")),
                IosVersionGroup("iOS 14", listOf("14.1", "14.5.1", "14.7.1", "14.8")),
                IosVersionGroup("iOS 15", listOf("15.0.1", "15.2", "15.4.1", "15.6.1", "15.7")),
                IosVersionGroup("iOS 16", listOf("16.0", "16.1.2", "16.3.1", "16.5.1", "16.6.1")),
                IosVersionGroup("iOS 17", listOf("17.0.3", "17.1.2", "17.2.1", "17.3.1", "17.4.1", "17.5.1", "17.6.1", "17.7.2")),
                IosVersionGroup("iOS 18", listOf("18.0", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2"))
            )
        ),
        SupportedDevice(
            id = "ip14pm_153",
            name = "iPhone 14 Pro Max",
            identifier = "iPhone15,3",
            isNew = true,
            hardwareCodes = listOf("A2651", "A2893", "A2896", "A2895"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 16", listOf("16.3.1", "16.4.1", "16.5", "16.6")),
                IosVersionGroup("iOS 17", listOf("17.0.3", "17.2.1", "17.3.1", "17.4.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip13pm_143",
            name = "iPhone 13 Pro Max",
            identifier = "iPhone14,3",
            isNew = true,
            hardwareCodes = listOf("A2484", "A2641", "A2644", "A2640"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 15", listOf("15.5", "15.6.1")),
                IosVersionGroup("iOS 16", listOf("16.0", "16.2", "16.3.1", "16.5.1", "16.6.1")),
                IosVersionGroup("iOS 17", listOf("17.1", "17.2.1", "17.3.1", "17.4.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip14p_152",
            name = "iPhone 14 Pro",
            identifier = "iPhone15,2",
            isNew = true,
            hardwareCodes = listOf("A2650", "A2889", "A2892", "A2891"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 16", listOf("16.5", "16.6.1")),
                IosVersionGroup("iOS 17", listOf("17.2.1", "17.3", "17.4.1", "17.5.1", "17.6.1", "17.7.1")),
                IosVersionGroup("iOS 18", listOf("18.0", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip12pm_134",
            name = "iPhone 12 Pro Max",
            identifier = "iPhone13,4",
            isNew = true,
            hardwareCodes = listOf("A2342", "A2410", "A2412", "A2411"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 15", listOf("15.1.1", "15.4.1", "15.6.1")),
                IosVersionGroup("iOS 16", listOf("16.0.3", "16.3", "16.4.1", "16.5.1", "16.6")),
                IosVersionGroup("iOS 17", listOf("17.0.3", "17.2.1", "17.3.1", "17.4.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.1")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipxs_112",
            name = "iPhone XS",
            identifier = "iPhone11,2",
            isNew = true,
            hardwareCodes = listOf("A2097", "A2098", "A1920", "A2099"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 12", listOf("12.2")),
                IosVersionGroup("iOS 14", listOf("14.2", "14.6", "14.7.1")),
                IosVersionGroup("iOS 15", listOf("15.1", "15.2.1", "15.3")),
                IosVersionGroup("iOS 16", listOf("16.1.1", "16.2", "16.4.1", "16.5.1")),
                IosVersionGroup("iOS 17", listOf("17.0.3", "17.1.1", "17.2.1", "17.3.1", "17.4.1", "17.5.1", "17.6.1", "17.7.1")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2"))
            )
        ),
        SupportedDevice(
            id = "ip13p_142",
            name = "iPhone 13 Pro",
            identifier = "iPhone14,2",
            isNew = true,
            hardwareCodes = listOf("A2483", "A2636", "A2639", "A2635"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 15", listOf("15.4.1", "15.5")),
                IosVersionGroup("iOS 16", listOf("16.1.1", "16.3.1", "16.5")),
                IosVersionGroup("iOS 17", listOf("17.1.1", "17.2.1", "17.4.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A340)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip8_104",
            name = "iPhone 8",
            identifier = "iPhone10,4",
            isNew = true,
            hardwareCodes = listOf("A1905"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 12", listOf("12.1.4", "12.3.1")),
                IosVersionGroup("iOS 13", listOf("13.3")),
                IosVersionGroup("iOS 15", listOf("15.1")),
                IosVersionGroup("iOS 16", listOf("16.0.2", "16.4.1", "16.5.1", "16.6", "16.7.5", "16.7.10", "16.7.16 (20H392)"))
            )
        ),
        SupportedDevice(
            id = "ipx_103",
            name = "iPhone X",
            identifier = "iPhone10,3",
            isNew = true,
            hardwareCodes = listOf("A1865", "A1902"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 14", listOf("14.8")),
                IosVersionGroup("iOS 15", listOf("15.5", "15.7")),
                IosVersionGroup("iOS 16", listOf("16.0.2", "16.2", "16.3.1", "16.5.1", "16.6", "16.7.8", "16.7.16 (20H392)"))
            )
        ),
        SupportedDevice(
            id = "ip7_93",
            name = "iPhone 7",
            identifier = "iPhone9,3",
            isNew = true,
            hardwareCodes = listOf("A1778"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 9", listOf("9.3.5")),
                IosVersionGroup("iOS 11", listOf("11.2.5")),
                IosVersionGroup("iOS 13", listOf("13.3.1")),
                IosVersionGroup("iOS 14", listOf("14.0.1", "14.4.2")),
                IosVersionGroup("iOS 15", listOf("15.0", "15.4.1", "15.6.1", "15.7.5", "15.8.4", "15.8.8 (19H422)"))
            )
        ),
        SupportedDevice(
            id = "ip16p_171",
            name = "iPhone 16 Pro",
            identifier = "iPhone17,1",
            isNew = true,
            hardwareCodes = listOf("A3291", "A3295"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 18", listOf("18.1.1", "18.3.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip16pm_172",
            name = "iPhone 16 Pro Max",
            identifier = "iPhone17,2",
            isNew = true,
            hardwareCodes = listOf("A3294", "A3298"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 18", listOf("18.1", "18.2.1", "18.4.1", "18.5", "18.6.2", "18.7")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip15p_161",
            name = "iPhone 15 Pro",
            identifier = "iPhone16,1",
            isNew = true,
            hardwareCodes = listOf("A3101", "A3286", "A3289", "A3288"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 17", listOf("17.3.1", "17.5.1", "17.6")),
                IosVersionGroup("iOS 18", listOf("18.0", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.1")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip15pm_162",
            name = "iPhone 15 Pro Max",
            identifier = "iPhone16,2",
            isNew = true,
            hardwareCodes = listOf("A3105", "A3291", "A3294", "A3293"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 17", listOf("17.2.1", "17.3.1", "17.4", "17.5.1", "17.6.1")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip15_154",
            name = "iPhone 15",
            identifier = "iPhone15,4",
            isNew = true,
            hardwareCodes = listOf("A3090", "A2846", "A3089", "A3092"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 17", listOf("17.4.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip13_145",
            name = "iPhone 13",
            identifier = "iPhone14,5",
            isNew = true,
            hardwareCodes = listOf("A2482", "A2631", "A2634", "A2633"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 15", listOf("15.5", "15.7.1")),
                IosVersionGroup("iOS 16", listOf("16.0.2", "16.4.1", "16.5.1", "16.6.1")),
                IosVersionGroup("iOS 17", listOf("17.0.3", "17.1.2", "17.2.1", "17.4.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip12_132",
            name = "iPhone 12",
            identifier = "iPhone13,2",
            isNew = true,
            hardwareCodes = listOf("A2172", "A2402", "A2404", "A2403"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 14", listOf("14.6", "14.8.1")),
                IosVersionGroup("iOS 15", listOf("15.1", "15.5", "15.6.1")),
                IosVersionGroup("iOS 16", listOf("16.0.3", "16.3.1", "16.5.1", "16.6.1")),
                IosVersionGroup("iOS 17", listOf("17.0.3", "17.1.2", "17.2.1", "17.4.1", "17.5.1", "17.6.1", "17.7.1")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipse2_128",
            name = "iPhone SE (2nd Gen)",
            identifier = "iPhone12,8",
            isNew = true,
            hardwareCodes = listOf("A2275", "A2296", "A2298"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 14", listOf("14.6", "14.8.1")),
                IosVersionGroup("iOS 15", listOf("15.1", "15.3.1", "15.5", "15.6.1")),
                IosVersionGroup("iOS 16", listOf("16.2", "16.3.1", "16.5.1", "16.6")),
                IosVersionGroup("iOS 17", listOf("17.0.2", "17.2.1", "17.3.1", "17.4.1", "17.5.1", "17.6.1")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipse3_146",
            name = "iPhone SE (3rd Gen)",
            identifier = "iPhone14,6",
            isNew = true,
            hardwareCodes = listOf("A2595", "A2782", "A2783", "A2784"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 16", listOf("16.1.1")),
                IosVersionGroup("iOS 17", listOf("17.4.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1", "18.2.1", "18.3", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ip6s_81",
            name = "iPhone 6s",
            identifier = "iPhone8,1",
            isNew = true,
            hardwareCodes = listOf("A1633", "A1688", "A1700"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 12", listOf("12.1.2", "12.4.1")),
                IosVersionGroup("iOS 14", listOf("14.2", "14.4.2")),
                IosVersionGroup("iOS 15", listOf("15.1", "15.3.1", "15.5", "15.7.9", "15.8.8 (19H422)")),
                IosVersionGroup("iOS 18", listOf("18.6 (22G86)"))
            )
        ),
        SupportedDevice(
            id = "ip5s_62",
            name = "iPhone 5s",
            identifier = "iPhone6,2",
            isNew = false,
            hardwareCodes = listOf("A1457", "A1518", "A1528", "A1530"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 9", listOf("9.0.1")),
                IosVersionGroup("iOS 10", listOf("10.0.2", "10.3.3")),
                IosVersionGroup("iOS 11", listOf("11.3")),
                IosVersionGroup("iOS 12", listOf("12.0.1", "12.4.8", "12.5.5", "12.5.8 (16H88)"))
            )
        ),
        SupportedDevice(
            id = "ip4s_41",
            name = "iPhone 4S",
            identifier = "iPhone4,1",
            isNew = false,
            hardwareCodes = listOf("A1387", "A1431"),
            category = DeviceType.IPHONE,
            iosVersions = listOf(
                IosVersionGroup("iOS 7", listOf("7.1.2")),
                IosVersionGroup("iOS 8", listOf("8.3")),
                IosVersionGroup("iOS 9", listOf("9.1", "9.3.5", "9.3.6 (13G37)"))
            )
        ),

        // iPads
        SupportedDevice(
            id = "ipad_air11_m2",
            name = "iPad Air 11\" (M2)",
            identifier = "iPad15,7",
            isNew = true,
            hardwareCodes = listOf("A2902", "A2903"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 18", listOf("18.5 (22F76)", "18.6 (22G86)", "18.6.2", "18.7.2")),
                IosVersionGroup("iOS 26", listOf("26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_pro11_4th",
            name = "iPad Pro 11\" (4th Gen)",
            identifier = "iPad14,4",
            isNew = true,
            hardwareCodes = listOf("A2759"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 18", listOf("18.0 (22A3354)", "18.3 (22D63)")),
                IosVersionGroup("iOS 26", listOf("26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_10th_1318",
            name = "iPad (10th Gen)",
            identifier = "iPad13,18",
            isNew = true,
            hardwareCodes = listOf("A2696", "A2757"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 17", listOf("17.3", "17.4.1", "17.5.1", "17.6.1")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_9th_121",
            name = "iPad (9th Gen)",
            identifier = "iPad12,1",
            isNew = true,
            hardwareCodes = listOf("A2602", "A2604"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 16", listOf("16.2", "16.5", "16.6.1")),
                IosVersionGroup("iOS 17", listOf("17.2", "17.3.1", "17.4.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0.1", "18.1.1", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A340)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_8th_116",
            name = "iPad (8th Gen)",
            identifier = "iPad11,6",
            isNew = true,
            hardwareCodes = listOf("A2270", "A2428", "A2429", "A2430"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 14", listOf("14.4", "14.6", "14.7.1")),
                IosVersionGroup("iOS 15", listOf("15.1", "15.5")),
                IosVersionGroup("iOS 16", listOf("16.3.1", "16.5.1", "16.6.1")),
                IosVersionGroup("iOS 17", listOf("17.1.1", "17.2", "17.3.1", "17.4.1", "17.5.1", "17.6.1")),
                IosVersionGroup("iOS 18", listOf("18.0", "18.1.1", "18.2.1", "18.3", "18.4.1", "18.5", "18.6.2", "18.7.1")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A341)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_7th_711",
            name = "iPad (7th Gen)",
            identifier = "iPad7,11",
            isNew = true,
            hardwareCodes = listOf("A2197"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 14", listOf("14.0.1", "14.4.1", "14.7.1")),
                IosVersionGroup("iOS 15", listOf("15.4.1", "15.5", "15.6.1", "15.7")),
                IosVersionGroup("iOS 16", listOf("16.1", "16.2", "16.3.1", "16.4.1", "16.6")),
                IosVersionGroup("iOS 17", listOf("17.2", "17.3.1", "17.5.1", "17.6.1", "17.7")),
                IosVersionGroup("iOS 18", listOf("18.0", "18.2.1", "18.3.2", "18.4.1", "18.5", "18.6.2", "18.7.2"))
            )
        ),
        SupportedDevice(
            id = "ipad_pro129_5th",
            name = "iPad Pro 12.9\" (5th Gen)",
            identifier = "iPad13,10",
            isNew = true,
            hardwareCodes = listOf("A2462", "A2379"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 15", listOf("15.3.1")),
                IosVersionGroup("iOS 18", listOf("18.2", "18.5 (22F76)")),
                IosVersionGroup("iOS 26", listOf("26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_air5_1316",
            name = "iPad Air (5th Gen)",
            identifier = "iPad13,16",
            isNew = true,
            hardwareCodes = listOf("A2588", "A2589"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 16", listOf("16.3.1", "16.4.1", "16.6")),
                IosVersionGroup("iOS 17", listOf("17.2")),
                IosVersionGroup("iOS 18", listOf("18.4.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_air4_131",
            name = "iPad Air (4th Gen)",
            identifier = "iPad13,1",
            isNew = true,
            hardwareCodes = listOf("A2316", "A2324", "A2325", "A2072"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 14", listOf("14.7.1")),
                IosVersionGroup("iOS 15", listOf("15.6.1")),
                IosVersionGroup("iOS 18", listOf("18.3.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.0 (23A5260n)", "26.0.1 (23A355)", "26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_air2_53",
            name = "iPad Air 2",
            identifier = "iPad5,3",
            isNew = true,
            hardwareCodes = listOf("A1566"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 15", listOf("15.3.1", "15.4.1", "15.6", "15.7.6", "15.8.4", "15.8.8 (19H422)"))
            )
        ),
        SupportedDevice(
            id = "ipad_mini6_142",
            name = "iPad mini (6th Gen)",
            identifier = "iPad14,2",
            isNew = false,
            hardwareCodes = listOf("A2568", "A2569"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 18", listOf("18.4.1", "18.6.2 (22G100)"))
            )
        ),
        SupportedDevice(
            id = "ipad_mini5_111",
            name = "iPad mini (5th Gen)",
            identifier = "iPad11,1",
            isNew = true,
            hardwareCodes = listOf("A2133", "A2124", "A2125", "A2126"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 15", listOf("15.0.1")),
                IosVersionGroup("iOS 16", listOf("16.3.1", "16.5")),
                IosVersionGroup("iOS 17", listOf("17.1.2", "17.3", "17.6.1")),
                IosVersionGroup("iOS 18", listOf("18.3.2", "18.4.1", "18.5", "18.6.2")),
                IosVersionGroup("iOS 26", listOf("26.1 (23B85)"))
            )
        ),
        SupportedDevice(
            id = "ipad_mini4_51",
            name = "iPad mini 4",
            identifier = "iPad5,1",
            isNew = true,
            hardwareCodes = listOf("A1538"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 11", listOf("11.3")),
                IosVersionGroup("iOS 14", listOf("14.4.2")),
                IosVersionGroup("iOS 15", listOf("15.7.5", "15.8.4", "15.8.8 (19H422)"))
            )
        ),
        SupportedDevice(
            id = "ipad_5th_611",
            name = "iPad (5th Gen)",
            identifier = "iPad6,11",
            isNew = false,
            hardwareCodes = listOf("A1822"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 12", listOf("12.0.1", "12.1.1")),
                IosVersionGroup("iOS 15", listOf("15.0.2")),
                IosVersionGroup("iOS 16", listOf("16.4", "16.7.5", "16.7.10", "16.7.16 (20H392)"))
            )
        ),
        SupportedDevice(
            id = "ipad_2_21",
            name = "iPad 2",
            identifier = "iPad2,1",
            isNew = false,
            hardwareCodes = listOf("A1395"),
            category = DeviceType.IPAD,
            iosVersions = listOf(
                IosVersionGroup("iOS 9", listOf("9.3.5 (13G36)"))
            )
        ),

        // iPod Touch
        SupportedDevice(
            id = "ipod_7th_91",
            name = "iPod touch (7th Gen)",
            identifier = "iPod9,1",
            isNew = true,
            hardwareCodes = listOf("A2178"),
            category = DeviceType.IPOD,
            iosVersions = listOf(
                IosVersionGroup("iOS 15", listOf("15.2 (19C56)", "15.6.1 (19G82)", "15.8.5", "15.8.7", "15.8.8 (19H422)"))
            )
        ),
        SupportedDevice(
            id = "ipod_6th_71",
            name = "iPod touch (6th Gen)",
            identifier = "iPod7,1",
            isNew = false,
            hardwareCodes = listOf("A1574"),
            category = DeviceType.IPOD,
            iosVersions = listOf(
                IosVersionGroup("iOS 12", listOf("12.5.7 (16H81)", "12.5.8 (16H88)"))
            )
        ),
        SupportedDevice(
            id = "ipod_5th_51",
            name = "iPod touch (5th Gen)",
            identifier = "iPod5,1",
            isNew = false,
            hardwareCodes = listOf("A1421", "A1509"),
            category = DeviceType.IPOD,
            iosVersions = listOf(
                IosVersionGroup("iOS 9", listOf("9.3.2 (13F69)", "9.3.5 (13G36)"))
            )
        )
    )
}
