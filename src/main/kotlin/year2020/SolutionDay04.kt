package year2020

import common.BaseSolution

fun main() = println(SolutionDay04().result())

class SolutionDay04 : BaseSolution() {

    override val day = 4
    override val year = 2020

    override fun task1(): String {
        return passports.count { it.hasRequiredFields }.toString()
    }

    override fun task2(): String {
        return passports.count { it.isValid }.toString()
    }

    private val birthYear = Regex("byr:(\\S+)", RegexOption.MULTILINE)
    private val issueYear = Regex("iyr:(\\S+)", RegexOption.MULTILINE)
    private val expirationYear = Regex("eyr:(\\S+)", RegexOption.MULTILINE)
    private val height = Regex("hgt:(\\S+)", RegexOption.MULTILINE)
    private val hairColor = Regex("hcl:(\\S+)", RegexOption.MULTILINE)
    private val eyeColor = Regex("ecl:(\\S+)", RegexOption.MULTILINE)
    private val passportId = Regex("pid:(\\S+)", RegexOption.MULTILINE)

    private val passports = input().split("\r\n\r\n").map {
        val birth = birthYear.find(it)?.groupValues?.get(1)
        val issue = issueYear.find(it)?.groupValues?.get(1)
        val expiration = expirationYear.find(it)?.groupValues?.get(1)
        val height = height.find(it)?.groupValues?.get(1)
        val hairColor = hairColor.find(it)?.groupValues?.get(1)
        val eyeColor = eyeColor.find(it)?.groupValues?.get(1)
        val passportId = passportId.find(it)?.groupValues?.get(1)
        Passport(birth, issue, expiration, height, hairColor, eyeColor, passportId)
    }

    data class Passport(
        val birthYear: String?,
        val issueYear: String?,
        val expirationYear: String?,
        val height: String?,
        val hairColor: String?,
        val eyeColor: String?,
        val passportId: String?,
    ) {
        private val validBirthYear = birthYear?.toIntOrNull() in 1920..2002
        private val validIssueYear = issueYear?.toIntOrNull() in 2010..2020
        private val validExpirationYear = expirationYear?.toIntOrNull() in 2020..2030
        private val validHeightRegex = Regex("(\\d+)(cm|in)")
        private val validHeight = height?.let {
            val result = validHeightRegex.find(it)
            if (result == null)
                false
            else {
                val (height, type) = result.destructured
                when (type) {
                    "cm" -> height.toInt() in 150..193
                    "in" -> height.toInt() in 59..76
                    else -> false
                }
            }
        } ?: false

        private val validHairColorRegex = Regex("#([0-9]|[a-f]){6}")
        private val validHairColor = hairColor?.let { validHairColorRegex.find(it) != null } ?: false
        private val validEyeColorRegex = Regex("amb|blu|brn|gry|grn|hzl|oth")
        private val validEyeColor = eyeColor?.let { validEyeColorRegex.find(it) != null } ?: false
        private val validPassportId = passportId?.length == 9 && passportId.toLong() <= 999_999_999L

        val hasRequiredFields = birthYear != null && issueYear != null && expirationYear != null &&
                height != null && hairColor != null && eyeColor != null && passportId != null

        val isValid = hasRequiredFields &&
                validBirthYear && validIssueYear && validExpirationYear &&
                validHeight && validHairColor && validEyeColor && validPassportId
    }
}