package hu.bme.aut.android.sporttracker.ui.common

enum class LanguageType {
    HUNGARIAN {
        override fun toString() = "Magyar"
    },
    ENGLISH {
        override fun toString() = "English"
    },
    GERMAN {
        override fun toString() = "Deutsch"
    },
    ESPANOL {
        override fun toString() = "Español"
    };

    abstract override fun toString(): String
}