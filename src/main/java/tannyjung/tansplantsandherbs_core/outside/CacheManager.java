package tannyjung.tansplantsandherbs_core.outside;

import tannyjung.tansplantsandherbs_core.Core;

import java.nio.ByteBuffer;
import java.util.*;

public class CacheManager {

    public static final Object lock = new Object();
    public static final Map<String, Map<String, String>> cache_map_text = new HashMap<>();
    public static final Map<String, Map<String, List<String>>> cache_map_text_list = new HashMap<>();
    public static final Map<String, Map<String, Boolean>> cache_map_logic = new HashMap<>();
    public static final Map<String, Map<String, short[]>> cache_map_number_short_list = new HashMap<>();
    public static final Map<String, Map<String, int[]>> cache_map_number_int_list = new HashMap<>();
    public static final Map<String, List<String>> cache_list_text = new HashMap<>();

    public static String clear () {

        String convert = "";
        int size = 0;

        synchronized (lock) {

            {

                size = size + SizeCalculation.getMapText(cache_map_text);
                cache_map_text.clear();

                size = size + SizeCalculation.getMapTextList(cache_map_text_list);
                cache_map_text_list.clear();

                size = size + SizeCalculation.getMapLogic(cache_map_logic);
                cache_map_logic.clear();

                size = size + SizeCalculation.getMapNumberShort(cache_map_number_short_list);
                cache_map_number_short_list.clear();

                size = size + SizeCalculation.getMapNumberInt(cache_map_number_int_list);
                cache_map_number_int_list.clear();

                size = size + SizeCalculation.getListText(cache_list_text);
                cache_list_text.clear();

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

    public static class SizeCalculation {

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

                    return_number = return_number + (entry2.getValue().length * Short.BYTES);

                }

            }

            return return_number;

        }

        public static int getMapNumberInt (Map<String, Map<String, int[]>> test) {

            int return_number = 0;

            for (Map.Entry<String, Map<String, int[]>> entry1 : test.entrySet()) {

                for (Map.Entry<String, int[]> entry2 : entry1.getValue().entrySet()) {

                    return_number = return_number + (entry2.getValue().length * Integer.BYTES);

                }

            }

            return return_number;

        }

        public static int getMapText (Map<String, Map<String, String>> test) {

            int return_number = 0;

            for (Map.Entry<String, Map<String, String>> entry1 : test.entrySet()) {

                for (Map.Entry<String, String> entry2 : entry1.getValue().entrySet()) {

                    return_number = return_number + (entry2.getValue().length() * Character.BYTES);

                }

            }

            return return_number;

        }

        public static int getMapTextList (Map<String, Map<String, List<String>>> test) {

            int return_number = 0;

            for (Map.Entry<String, Map<String, List<String>>> entry1 : test.entrySet()) {

                for (Map.Entry<String, List<String>> entry2 : entry1.getValue().entrySet()) {

                    for (String read_all : entry2.getValue()) {

                        return_number = return_number + (read_all.length() * Character.BYTES);

                    }

                }

            }

            return return_number;

        }

        public static int getListText (Map<String, List<String>> test) {

            int return_number = 0;

            for (Map.Entry<String, List<String>> entry : test.entrySet()) {

                for (String read_all : entry.getValue()) {

                    return_number = return_number + (read_all.length() * Character.BYTES);

                }

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

    public static List<String> getFunction (String path) {

        synchronized (lock) {

            if (cache_map_text_list.containsKey("functions") == false) {

                cache_map_text_list.put("functions", new HashMap<>());

            }

            if (cache_map_text_list.get("functions").containsKey(path) == false) {

                List<String> data = FileManager.readTXT(Core.path_config + "/#dev/#temporary/" + path + ".txt");
                cache_map_text_list.get("functions").put(path, data);

            }

        }

        return cache_map_text_list.get("functions").get(path);

    }

    public static String getDictionary (String key, boolean is_number) {

        synchronized (lock) {

            if (cache_map_text.containsKey("dictionary") == false) {

                cache_map_text.put("dictionary", new HashMap<>());

            }

            if (cache_map_text.get("dictionary").containsKey(key) == false) {

                String value_id = "";
                String value_text = "";

                // Get Data
                {

                    String path = Core.path_world_mod + "/dictionary.txt";
                    List<String> data = FileManager.readTXT(path);

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

                            value_id = String.valueOf(data.size() + 1);
                            FileManager.writeTXT(path, value_id + "|" + value_text + "\n", true);

                        }

                    }

                }

                cache_map_text.get("dictionary").put(value_id, value_text);
                cache_map_text.get("dictionary").put(value_text, value_id);

            }

        }

        return cache_map_text.get("dictionary").get(key);

    }

    public static class Result {

        public static boolean existLogic (String name, String key) {

            synchronized (lock) {

                return cache_map_logic.getOrDefault(name, new HashMap<>()).containsKey(key);

            }

        }

        public static boolean getLogic (String name, String key) {

            synchronized (lock) {

                return cache_map_logic.getOrDefault(name, new HashMap<>()).getOrDefault(key, false);

            }

        }

        public static void setLogic (String name, String key, boolean value) {

            synchronized (lock) {

                cache_map_logic.computeIfAbsent(name, test -> new HashMap<>()).put(key, value);

            }

        }

    }

    public static class SaveMap {

        public static boolean existLogic (String name, String key) {

            synchronized (lock) {

                return cache_map_logic.containsKey(name) == true && cache_map_logic.get(name).containsKey(key) == true;

            }

        }

        public static boolean getLogic (String name, String key) {

            synchronized (lock) {

                return cache_map_logic.get(name).get(key);

            }

        }

        public static void setLogic (String name, String key, boolean value) {

            synchronized (lock) {

                cache_map_logic.computeIfAbsent(name, test -> new HashMap<>()).put(key, value);

            }

        }

        public static boolean existTextList (String name, String key) {

            synchronized (lock) {

                return cache_map_text_list.containsKey(name) == true && cache_map_text_list.get(name).containsKey(key) == true;

            }

        }

        public static List<String> getTextList (String name, String key) {

            synchronized (lock) {

                return cache_map_text_list.get(name).get(key);

            }

        }

        public static void setTextList (String name, String key, List<String> value) {

            synchronized (lock) {

                cache_map_text_list.computeIfAbsent(name, test -> new HashMap<>()).put(key, value);

            }

        }

    }

    public static class SaveList {

        public static boolean existText (String name) {

            synchronized (lock) {

                return cache_list_text.containsKey(name) == true;

            }

        }

        public static List<String> getText (String name) {

            synchronized (lock) {

                return cache_list_text.get(name);

            }

        }

        public static void setText (String name, List<String> value) {

            synchronized (lock) {

                cache_list_text.computeIfAbsent(name, test -> new ArrayList<>()).addAll(value);

            }

        }

    }

}
