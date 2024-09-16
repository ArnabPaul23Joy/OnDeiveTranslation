package com.example.mytranslationapplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TranslationConstants {
    private static final String[] CODE_ARRAY = {
            "af", "ar", "be", "bg", "bn", "ca", "cs", "cy", "da", "de",
            "el", "en", "eo", "es", "et", "fa", "fi", "fr", "ga", "gl",
            "gu", "he", "hi", "hr", "ht", "hu", "id", "is", "it", "ja",
            "ka", "kn", "ko", "lt", "lv", "mk", "mr", "ms", "mt", "nl",
            "no", "pl", "pt", "ro", "ru", "sk", "sl", "sq", "sv", "sw",
            "ta", "te", "th", "tl", "tr", "uk", "ur", "vi", "zh"
    };
    private static final String[] NAME_ARRAY = {
            "Afrikaans", "Arabic", "Belarusian", "Bulgarian", "Bengali", "Catalan",
            "Czech", "Welsh", "Danish", "German", "Greek", "English", "Esperanto",
            "Spanish", "Estonian", "Persian", "Finnish", "French", "Irish",
            "Galician", "Gujarati", "Hebrew", "Hindi", "Croatian", "Haitian",
            "Hungarian", "Indonesian", "Icelandic", "Italian", "Japanese", "Georgian",
            "Kannada", "Korean", "Lithuanian", "Latvian", "Macedonian", "Marathi",
            "Malay", "Maltese", "Dutch", "Norwegian", "Polish", "Portuguese", "Romanian", "Russian",
            "Slovak", "Slovenian", "Albanian", "Swedish", "Swahili", "Tamil", "Telugu", "Thai", "Tagalog",
            "Turkish", "Ukrainian", "Urdu", "Vietnamese", "Chinese"
    };
    public static final ArrayList<String> LANGUAGE_NAMES = new ArrayList<>(Arrays.asList(NAME_ARRAY));
    public static final ArrayList<String> LANGUAGE_CODES = new ArrayList<>(Arrays.asList(CODE_ARRAY));
}
