package com.vereshchagin.nikolay.stankinschedule.resources

/**
 * Ресурсы для тестирования пар и расписаний.
 */
object PairResources {

    /**
     * Список пар для тестирования.
     */
    val PAIRS = listOf(
        """
            {
                "title": "Иностранный язык",
                "lecturer": "Стихова О.В",
                "type": "Seminar",
                "subgroup": "Common",
                "classroom": "",
                "time": {
                    "start": "12:20",
                    "end": "14:00"
                },
                "dates": [
                    {
                        "frequency": "every",
                        "date": "2019.02.04-2019.05.20"
                    }
                ]
            }
            """,
        """
            {
                "title": "Прикладная физическая культура",
                "lecturer": "",
                "type": "Seminar",
                "subgroup": "Common",
                "classroom": "С/З СТАНКИН",
                "time": {
                    "start": "12:20",
                    "end": "14:00"
                },
                "dates": [
                    {
                        "frequency": "every",
                        "date": "2019.02.11-2019.05.20"
                    }
                ]
            }
            """,
        """
            {
                "title": "Иностранный язык",
                "lecturer": "Стихова О.В",
                "type": "Seminar",
                "subgroup": "Common",
                "classroom": "",
                "time": {
                    "start": "14:10",
                    "end": "15:50"
                },
                "dates": [
                    {
                        "frequency": "every",
                        "date": "2019.02.04-2019.05.20"
                    }
                ]
            }
            """,
        """
            {
                "title": "Иностранный язык",
                "lecturer": "Стихова О.В",
                "type": "Seminar",
                "subgroup": "A",
                "classroom": "",
                "time": {
                    "start": "14:10",
                    "end": "15:50"
                },
                "dates": [
                    {
                        "frequency": "every",
                        "date": "2019.02.04-2019.05.20"
                    }
                ]
            }
            """,
        """
            {
                "title": "Иностранный язык",
                "lecturer": "Стихова О.В",
                "type": "Seminar",
                "subgroup": "B",
                "classroom": "",
                "time": {
                    "start": "14:10",
                    "end": "15:50"
                },
                "dates": [
                    {
                        "frequency": "every",
                        "date": "2019.02.11-2019.05.20"
                    }
                ]
            }
            """,
        """
            {
                "title": "Иностранный язык",
                "lecturer": "Стихова О.В",
                "type": "Lecture",
                "subgroup": "Common",
                "classroom": "",
                "time": {
                    "start": "10:20",
                    "end": "15:50"
                },
                "dates": [
                    {
                        "frequency": "every",
                        "date": "2019.02.11-2019.05.20"
                    }
                ]
            }
            """
    )
}