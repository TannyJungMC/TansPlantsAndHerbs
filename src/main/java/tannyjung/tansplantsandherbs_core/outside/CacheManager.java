package tannyjung.tansplantsandherbs_core.outside;

import tannyjung.tansplantsandherbs_core.Core;

import java.nio.ByteBuffer;
import java.util.*;

public class CacheManager {

    public static final Object lock = new Object();
    public static final Map<String, Map<String, String>> cache_string = new HashMap<>();
    public static final Map<String, Map<String, Boolean>> cache_logic = new HashMap<>();
    public static final Map<String, Map<String, String[]>> cache_string_list = new HashMap<>();
    public static final Map<String, Map<String, short[]>> cache_number_short_list = new HashMap<>();
    public static final Map<String, Map<String, int[]>> cache_number_int_list = new HashMap<>();

    public static double clear () {

        int size = 0;

        synchronized (lock) {

            {

                size = size + Size.getMapText(cache_string);
                cache_string.clear();

                size = size + Size.getMapLogic(cache_logic);
                cache_logic.clear();

                size = size + Size.getMapTextList(cache_string_list);
                cache_string_list.clear();

                size = size + Size.getMapNumberShort(cache_number_short_list);
                cache_number_short_list.clear();

                size = size + Size.getMapNumberInt(cache_number_int_list);
                cache_number_int_list.clear();

            }

        }

        return Double.parseDouble(String.format(Locale.US, "%.2f", (double) size / (1024 * 1024)));

    }

    public static String getDictionary (String key, boolean is_number) {

        synchronized (lock) {

            if (cache_string.containsKey("dictionary") == false) {

                cache_string.put("dictionary", new HashMap<>());

            }

            if (cache_string.get("dictionary").containsKey(key) == false) {

                String value_id = "";
                String value_text = "";

                // Get Data
                {

                    String path = Core.path_world_mod + "/dictionary.txt";
                    String[] data = FileManager.readTXT(path);

                    for (String read_all : data) {

                        if (is_number == true) {

                            if (read_all.startsWith(key + "|") == true) {

                                value_id = key;
                                value_text = read_all.substring(read_all.indexOf("|") + 1);
                                break;

                            }

                        } else {

                            if (read_all.endsWith("|" + key) == true) {

                                value_id = read_all.substring(0, read_all.indexOf("|"));
                                value_text = key;
                                break;

                            }

                        }

                    }

                    if (value_id.isEmpty() == true && value_text.isEmpty() == true) {

                        if (is_number == false) {

                            value_text = key;

                        }

                        if (value_text.isEmpty() == false) {

                            value_id = String.valueOf(data.length + 1);
                            FileManager.writeTXT(path, value_id + "|" + value_text + "\n", true);

                        }

                    }

                }

                cache_string.get("dictionary").put(value_id, value_text);
                cache_string.get("dictionary").put(value_text, value_id);

            }

        }

        return cache_string.get("dictionary").get(key);

    }

    public static String[] getFunction (String id) {

        synchronized (lock) {

            if (cache_string_list.containsKey("functions") == false) {

                cache_string_list.put("functions", new HashMap<>());

            }

            if (cache_string_list.get("functions").containsKey(id) == false) {

                String[] data = FileManager.readTXT(Core.path_config + "/#dev/#temporary/" + id + ".txt");
                cache_string_list.get("functions").put(id, data);

            }

        }

        return cache_string_list.get("functions").get(id);

    }

    public static class SaveData {

        public static boolean existLogic (String name, String id) {

            synchronized (lock) {

                if (cache_logic.containsKey(name) == false) {

                    cache_logic.put(name, new HashMap<>());

                }

                return cache_logic.get(name).containsKey(id);

            }

        }

        public static boolean getLogic (String name, String id) {

            synchronized (lock) {

                if (cache_logic.containsKey(name) == false) {

                    cache_logic.put(name, new HashMap<>());

                }

                return cache_logic.get(name).getOrDefault(id, false);

            }

        }

        public static void setLogic (String name, String id, boolean value) {

            synchronized (lock) {

                if (cache_logic.containsKey(name) == false) {

                    cache_logic.put(name, new HashMap<>());

                }

                cache_logic.get(name).put(id, value);

            }

        }

    }

    public static class Size {

        public static int getMapByteBuffer (Map<String, ByteBuffer> test) {

            int return_number = 0;

            for (Map.Entry<String, ByteBuffer> entry : test.entrySet()) {

                return_number = return_number + entry.getValue().capacity();

            }

            return return_number;

        }

        public static int getMapNumberShort (Map<String, Map<String, short[]>> test) {

            int return_number = 0;

            for (Map.Entry<String, Map<String, short[]>> entry1 : test.entrySet()) {

                for (Map.Entry<String, short[]> entry2 : entry1.getValue().entrySet()) {

                    return_number = return_number + entry2.getValue().length * Short.BYTES;

                }

            }

            return return_number;

        }

        public static int getMapNumberInt (Map<String, Map<String, int[]>> test) {

            int return_number = 0;

            for (Map.Entry<String, Map<String, int[]>> entry1 : test.entrySet()) {

                for (Map.Entry<String, int[]> entry2 : entry1.getValue().entrySet()) {

                    return_number = return_number + entry2.getValue().length * Integer.BYTES;

                }

            }

            return return_number;

        }

        public static int getMapText (Map<String, Map<String, String>> test) {

            int return_number = 0;

            for (Map.Entry<String, Map<String, String>> entry1 : test.entrySet()) {

                for (Map.Entry<String, String> entry2 : entry1.getValue().entrySet()) {

                    return_number = return_number + entry2.getValue().length() * Character.BYTES;

                }

            }

            return return_number;

        }

        public static int getMapTextList (Map<String, Map<String, String[]>> test) {

            int return_number = 0;

            for (Map.Entry<String, Map<String, String[]>> entry1 : test.entrySet()) {

                for (Map.Entry<String, String[]> entry2 : entry1.getValue().entrySet()) {

                    return_number = return_number + entry2.getValue().length * Integer.BYTES;

                }

            }

            return return_number;

        }

        public static int getArrayText (String[] test) {

            int return_number = 0;

            for (String get : test) {

                return_number = return_number + get.length() * Integer.BYTES;

            }

            return return_number;

        }

        public static int getMapLogic (Map<String, Map<String, Boolean>> test) {

            int return_number = 0;

            for (Map.Entry<String, Map<String, Boolean>> entry1 : test.entrySet()) {

                return_number = return_number + entry1.getValue().size();

            }

            return return_number;

        }

    }

}
