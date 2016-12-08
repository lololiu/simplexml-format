package com.royll.xmlformat.config;

/**
 * Created by dim on 15/5/31.
 */
public class Constant {

    public static final String privateStr = "   private String name;\n" +
            "\n" +
            "    public void setName(String name){\n" +
            "        this.name=name;\n" +
            "    }\n" +
            "\n" +
            "    public String getName(){\n" +
            "        return name;\n" +
            "    }";
    public static final String publicStr = "    public String name;";

    public static final String privateUseSerializedNameStr = "    @SerializedName(\"name\")\n" +
            "    private String name;\n" +
            "\n" +
            "    public void setName(String name){\n" +
            "        this.name=name;\n" +
            "    }\n" +
            "\n" +
            "    public String getName(){\n" +
            "        return name;\n" +
            "    }";

    public static final String publicUseSerializedNameStr = "    @SerializedName(\"name\")\n" +
            "    public String name;";

    public static final String objectFromObject = "    public  static $ClassName$ objectFromData(String str){\n" +
            "\n" +
            "        return new com.google.gson.Gson().fromJson(str,$ClassName$.class);\n" +
            "    }";

    public static final String objectFromObject1 = "    public  static $ClassName$ objectFromData(String str, String key){\n" +
            "\n" +
            "        try {\n" +
            "            org.org.json.JSONObject jsonObject=new org.org.json.JSONObject(str);\n" +
            "\n" +
            "            return new com.google.gson.Gson().fromJson(jsonObject.getString(str),$ClassName$.class);\n" +
            "        } catch (org.org.json.JSONException e) {\n" +
            "            e.printStackTrace();\n" +
            "        }\n" +
            "\n" +
            "        return null;\n" +
            "    }";

    public static final String arrayFromData = "    public  static java.util.List<$ClassName$> array$ClassName$FromData(String str){\n" +
            "\n" +
            "        java.lang.reflect.Type listType=new com.google.gson.reflect.TypeToken<java.util.ArrayList<$ClassName$>>(){}.getType();\n" +
            "\n" +
            "        return new com.google.gson.Gson().fromJson(str,listType);\n" +
            "    }";

    public static final String arrayFromData1 = "    public  static java.util.List<$ClassName$> array$ClassName$FromData(String str,String key){\n" +
            "\n" +
            "        try {\n" +
            "            org.org.json.JSONObject jsonObject=new org.org.json.JSONObject(str);\n" +
            "            java.lang.reflect.Type listType=new com.google.gson.reflect.TypeToken<java.util.ArrayList<$ClassName$>>(){}.getType();\n" +
            "\n" +
            "            return new com.google.gson.Gson().fromJson(jsonObject.getString(str),listType);\n" +
            "\n" +
            "        } catch (org.org.json.JSONException e) {\n" +
            "            e.printStackTrace();\n" +
            "        }\n" +
            "\n" +
            "        return new java.util.ArrayList();\n" +
            "\n" +
            "\n" +
            "    }";

    public static final String xmlAnnotation = "@org.simpleframework.xml.Element\\s*\\(\\s*name\\s*=\\s*\"{filed}\"\\s*\\,required = false)";

    public static final String xmlFullNameAnnotation = "@org.simpleframework.xml.Element(name=\"{filed}\",required = false)";

    public static final String xmlListAnnotation = "@org.simpleframework.xml.ElementList(name=\"{filed}\",required = false)";
    public static final String xmlClassAnnotation = "@org.simpleframework.xml.Root(name=\"{filed}\")";

}
