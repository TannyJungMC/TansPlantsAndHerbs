package tannyjung.tansplantsandherbs_core;

import tannyjung.tansplantsandherbs_core.outside.FileManager;

import java.nio.ByteBuffer;
import java.util.*;

public class CacheManager {

    public static final Object lock = new Object();
    public static final Map<String, Map<String, String>> cache_string = new HashMap<>();
    public static final Map<String, Map<String, Boolean>> cache_logic = new HashMap<>();
    public static final Map<String, Map<String, String[]>> cache_string_list = new HashMap<>();
    public static final Map<String, Map<String, short[]>> cache_number_short_list = new HashMap<>();
    public static final Map<String, Map<String, int[]>> cache_number_int_list = new HashMap<>();
    public static final Map<String, Map<Integer, Object>> cache_dictionary_object = new HashMap<>();

    public static String clear () {

        String convert = "";
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

                // TODO -> size for object cache
                cache_dictionary_object.clear();

            }

        }

        if (size < 1024) {

            convert = size + " B";

        } else if (size < 1048576) {

            convert = String.format(Locale.US, "%.2f", (double) size / 1024.0) + " KB";

        } else {

            convert = String.format(Locale.US, "%.2f", (double) size / 1048576.0) + " MB";

        }

        return convert;

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
    
    public static class Dictionary {

        public static String getSavable (String key, boolean is_number) {

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
        
        public static Object getUnsavable (String name, int id) {

            synchronized (lock) {

                Object a = cache_dictionary_object.get(name).get(id);

                return cache_dictionary_object.get(name).get(id);

            }
            
        }

        public static int setUnsavable (String name, Object object) {

            synchronized (lock) {

                if (cache_dictionary_object.containsKey(name) == false) {

                    cache_dictionary_object.put(name, new HashMap<>());

                }

                if (cache_dictionary_object.get(name).containsValue(object) == false) {

                    cache_dictionary_object.get(name).put(cache_dictionary_object.size(), object);
                    return cache_dictionary_object.size();

                }

            }

            return 0;

        }

    }

    public static class Results {

        public static boolean containLogic (String name, String id) {

            synchronized (lock) {

                if (cache_logic.containsKey(name) == false) {

                    cache_logic.put(name, new HashMap<>());

                }

                return cache_logic.get(name).containsKey(id);

            }

        }

        public static boolean getLogic (String name, String id) {

            synchronized (lock) {

                return cache_logic.get(name).get(id);

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
