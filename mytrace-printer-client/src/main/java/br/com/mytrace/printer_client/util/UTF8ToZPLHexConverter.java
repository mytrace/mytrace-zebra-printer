package br.com.mytrace.printer_client.util;

import java.util.HashMap;
import java.util.Map;

public class UTF8ToZPLHexConverter {

	private final Map<Character, String> UTF8_HEX_SYMBOLS_MAP = new HashMap<Character, String>();

	public UTF8ToZPLHexConverter() {
		UTF8_HEX_SYMBOLS_MAP.put('À', "A");
		UTF8_HEX_SYMBOLS_MAP.put('Á', "A");
		UTF8_HEX_SYMBOLS_MAP.put('Ã', "A");
		UTF8_HEX_SYMBOLS_MAP.put('Å', "A");
		UTF8_HEX_SYMBOLS_MAP.put('Ç', "C");
		UTF8_HEX_SYMBOLS_MAP.put('É', "E");
		UTF8_HEX_SYMBOLS_MAP.put('Ê', "E");
		UTF8_HEX_SYMBOLS_MAP.put('Í', "I");
		UTF8_HEX_SYMBOLS_MAP.put('Ó', "O");
		UTF8_HEX_SYMBOLS_MAP.put('Ô', "O");
		UTF8_HEX_SYMBOLS_MAP.put('Õ', "O");
		UTF8_HEX_SYMBOLS_MAP.put('Ú', "U");
		UTF8_HEX_SYMBOLS_MAP.put('Û', "U");
		UTF8_HEX_SYMBOLS_MAP.put('á', "a");
		UTF8_HEX_SYMBOLS_MAP.put('â', "a");
		UTF8_HEX_SYMBOLS_MAP.put('ã', "a");
		UTF8_HEX_SYMBOLS_MAP.put('ç', "c");
		UTF8_HEX_SYMBOLS_MAP.put('é', "e");
		UTF8_HEX_SYMBOLS_MAP.put('í', "i");
		UTF8_HEX_SYMBOLS_MAP.put('î', "i");
		UTF8_HEX_SYMBOLS_MAP.put('ó', "o");
		UTF8_HEX_SYMBOLS_MAP.put('ô', "o");
		UTF8_HEX_SYMBOLS_MAP.put('õ', "o");
		UTF8_HEX_SYMBOLS_MAP.put('ú', "u");
		UTF8_HEX_SYMBOLS_MAP.put('û', "uå");
	}

	public String convert(String str) {

		char[] cArry = str.toCharArray();

		StringBuilder sb = new StringBuilder();
		for (char c : cArry) {
			String fromMap = UTF8_HEX_SYMBOLS_MAP.get(c);

			if (fromMap != null) {
				sb.append(fromMap);
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}

}