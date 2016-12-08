package com.royll.xmlformat.config;

import com.intellij.ide.util.PropertiesComponent;

/**
 * Created by dim on 15/5/31.
 */
public class Config {

    private static Config config;

    private boolean fieldPrivateMode = true;
    private boolean generateComments = true;
    private boolean useSerializedName = false;
    private boolean objectFromData = false;
    private boolean objectFromData1 = false;
    private boolean arrayFromData = false;
    private boolean arrayFromData1 = false;
    private boolean reuseEntity = false;
    private boolean virgoMode = true; //处女座模式
    private boolean useFieldNamePrefix = false;
    private boolean splitGenerate = false;

    private String rootElementName="EPOSPROTOCOL";


    private String objectFromDataStr;
    private String objectFromDataStr1;
    private String arrayFromDataStr;
    private String arrayFromData1Str;
    private String annotationStr; //注解语句
    private String filedNamePreFixStr; //字段前缀
    private String entityPackName;//创建实体类的包名.
    private String suffixStr;

    /**
     * 错误次数,前两次提醒哪里查看错误日志.
     */
    private int errorCount;


    private Config() {

    }

    public void save() {

        PropertiesComponent.getInstance().setValue("xfieldPrivateMode", "" + isFieldPrivateMode());
        PropertiesComponent.getInstance().setValue("xuseSerializedName", isUseSerializedName() + "");
        PropertiesComponent.getInstance().setValue("xobjectFromData", objectFromData + "");
        PropertiesComponent.getInstance().setValue("xobjectFromData1", objectFromData1 + "");
        PropertiesComponent.getInstance().setValue("xarrayFromData", arrayFromData + "");
        PropertiesComponent.getInstance().setValue("xarrayFromData1", arrayFromData1 + "");
        PropertiesComponent.getInstance().setValue("xobjectFromDataStr", objectFromDataStr + "");
        PropertiesComponent.getInstance().setValue("xobjectFromDataStr1", objectFromDataStr1 + "");
        PropertiesComponent.getInstance().setValue("xarrayFromData1Str", arrayFromData1Str + "");
        PropertiesComponent.getInstance().setValue("xsuffixStr", suffixStr + "");
        PropertiesComponent.getInstance().setValue("xreuseEntity", reuseEntity + "");
        PropertiesComponent.getInstance().setValue("xvirgoMode", virgoMode + "");
        PropertiesComponent.getInstance().setValue("xfiledNamePreFixStr", filedNamePreFixStr + "");
        PropertiesComponent.getInstance().setValue("xannotationStr", annotationStr + "");
        PropertiesComponent.getInstance().setValue("xerrorCount", errorCount + "");
        PropertiesComponent.getInstance().setValue("xentityPackName", entityPackName + "");
        PropertiesComponent.getInstance().setValue("xuseFieldNamePrefix", useFieldNamePrefix + "");
        PropertiesComponent.getInstance().setValue("xgenerateComments", generateComments + "");
        PropertiesComponent.getInstance().setValue("xsplitGenerate", splitGenerate + "");
    }

    public static Config getInstant() {

        if (config == null) {
            config = new Config();
            config.setFieldPrivateMode(PropertiesComponent.getInstance().getBoolean("xfieldPrivateMode", true));
            config.setUseSerializedName(PropertiesComponent.getInstance().getBoolean("xuseSerializedName", false));
            config.setObjectFromData(PropertiesComponent.getInstance().getBoolean("xobjectFromData", false));
            config.setObjectFromData1(PropertiesComponent.getInstance().getBoolean("xobjectFromData1", false));
            config.setArrayFromData(PropertiesComponent.getInstance().getBoolean("xarrayFromData", false));
            config.setArrayFromData1(PropertiesComponent.getInstance().getBoolean("xarrayFromData1", false));
            config.setSuffixStr(PropertiesComponent.getInstance().getValue("xsuffixStr", "Bean"));
            config.setReuseEntity(PropertiesComponent.getInstance().getBoolean("xreuseEntity", false));
            config.setObjectFromDataStr(PropertiesComponent.getInstance().getValue("xobjectFromDataStr", Constant.objectFromObject));
            config.setObjectFromDataStr1(PropertiesComponent.getInstance().getValue("xobjectFromDataStr1", Constant.objectFromObject1));
            config.setArrayFromDataStr(PropertiesComponent.getInstance().getValue("xarrayFromDataStr", Constant.arrayFromData));
            config.setArrayFromData1Str(PropertiesComponent.getInstance().getValue("xarrayFromData1Str", Constant.arrayFromData1));
            config.setAnnotationStr(PropertiesComponent.getInstance().getValue("xannotationStr", Constant.xmlAnnotation));
            config.setEntityPackName(PropertiesComponent.getInstance().getValue("xentityPackName"));
            config.setFiledNamePreFixStr(PropertiesComponent.getInstance().getValue("xfiledNamePreFixStr"));
            config.setErrorCount(PropertiesComponent.getInstance().getOrInitInt("xerrorCount", 0));
            config.setVirgoMode(PropertiesComponent.getInstance().getBoolean("xvirgoMode", true));
            config.setUseFieldNamePrefix(PropertiesComponent.getInstance().getBoolean("xuseFieldNamePrefix", false));
            config.setGenerateComments(PropertiesComponent.getInstance().getBoolean("xgenerateComments", true));
            config.setSplitGenerate(PropertiesComponent.getInstance().getBoolean("xsplitGenerate", false));
        }
        return config;
    }

    public String getRootElementName() {
        return rootElementName;
    }

    public void setRootElementName(String rootElementName) {
        this.rootElementName = rootElementName;
    }

    public boolean isUseFieldNamePrefix() {
        return useFieldNamePrefix;
    }

    public void setUseFieldNamePrefix(boolean useFieldNamePrefix) {
        this.useFieldNamePrefix = useFieldNamePrefix;
    }

    public boolean isObjectFromData() {
        return objectFromData;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getEntityPackName() {
        return entityPackName;
    }

    public String geFullNameAnnotation() {

        if (annotationStr.equals(Constant.xmlAnnotation)) {
            return Constant.xmlFullNameAnnotation;
        }
        return annotationStr.replaceAll("\\(", "(").replaceAll("\\)", ")").replaceAll("\\s\\*", "");
    }

    public String getFieldListAnnotation(){
        return Constant.xmlListAnnotation;
    }
    public String getClassAnnotation(){
        return Constant.xmlClassAnnotation;
    }
    public boolean isGenerateComments() {
        return generateComments;
    }

    public void setGenerateComments(boolean generateComments) {
        this.generateComments = generateComments;
    }

    public void setEntityPackName(String entityPackName) {
        this.entityPackName = entityPackName;
    }

    public boolean isVirgoMode() {
        return virgoMode;
    }

    public void setVirgoMode(boolean virgoMode) {
        this.virgoMode = virgoMode;
    }

    public String getFiledNamePreFixStr() {
        return filedNamePreFixStr;
    }

    public void setFiledNamePreFixStr(String filedNamePreFixStr) {
        this.filedNamePreFixStr = filedNamePreFixStr;
    }

    public String getAnnotationStr() {
        return annotationStr;
    }

    public void setAnnotationStr(String annotationStr) {
        this.annotationStr = annotationStr;
    }

    public void setObjectFromData(boolean objectFromData) {
        this.objectFromData = objectFromData;
    }

    public boolean isObjectFromData1() {
        return objectFromData1;
    }

    public void setObjectFromData1(boolean objectFromData2) {
        this.objectFromData1 = objectFromData2;
    }

    public boolean isArrayFromData() {
        return arrayFromData;
    }

    public void setArrayFromData(boolean arrayFromData) {
        this.arrayFromData = arrayFromData;
    }

    public boolean isArrayFromData1() {
        return arrayFromData1;
    }

    public void setArrayFromData1(boolean arrayFromData1) {
        this.arrayFromData1 = arrayFromData1;
    }


    public void setObjectFromDataStr(String objectFromDataStr) {
        this.objectFromDataStr = objectFromDataStr;
    }

    public void setObjectFromDataStr1(String objectFromDataStr1) {
        this.objectFromDataStr1 = objectFromDataStr1;
    }

    public void setArrayFromDataStr(String arrayFromDataStr) {
        this.arrayFromDataStr = arrayFromDataStr;
    }

    public void setArrayFromData1Str(String arrayFromData1Str) {
        this.arrayFromData1Str = arrayFromData1Str;
    }

    public String getObjectFromDataStr() {
        return objectFromDataStr;
    }

    public String getObjectFromDataStr1() {
        return objectFromDataStr1;
    }

    public String getArrayFromDataStr() {
        return arrayFromDataStr;
    }

    public String getArrayFromData1Str() {
        return arrayFromData1Str;
    }

    public String getSuffixStr() {
        return suffixStr;
    }

    public void setSuffixStr(String suffixStr) {
        this.suffixStr = suffixStr;
    }

    public boolean isReuseEntity() {
        return reuseEntity;
    }

    public void setReuseEntity(boolean reuseEntity) {
        this.reuseEntity = reuseEntity;
    }

    public boolean isUseSerializedName() {
        return useSerializedName;
    }

    public void setUseSerializedName(boolean useSerializedName) {
        this.useSerializedName = useSerializedName;
    }

    public boolean isFieldPrivateMode() {
        return fieldPrivateMode;
    }

    public void setFieldPrivateMode(boolean fieldPrivateMode) {
        this.fieldPrivateMode = fieldPrivateMode;
    }


    public void saveObjectFromDataStr(String objectFromDataStr) {
        this.objectFromDataStr = objectFromDataStr;
        PropertiesComponent.getInstance().setValue("xobjectFromDataStr", objectFromDataStr + "");
    }

    public void saveObjectFromDataStr1(String objectFromDataStr1) {
        this.objectFromDataStr1 = objectFromDataStr1;
        PropertiesComponent.getInstance().setValue("xobjectFromDataStr1", objectFromDataStr1 + "");
    }

    public void saveArrayFromDataStr(String arrayFromDataStr) {
        this.arrayFromDataStr = arrayFromDataStr;
        PropertiesComponent.getInstance().setValue("xarrayFromDataStr", arrayFromDataStr + "");
    }

    public void saveArrayFromData1Str(String arrayFromData1Str) {
        this.arrayFromData1Str = arrayFromData1Str;
        PropertiesComponent.getInstance().setValue("xarrayFromData1Str", arrayFromData1Str + "");
    }

    public boolean isToastError() {
        if (Config.getInstant().getErrorCount() < 3) {
            Config.getInstant().setErrorCount(Config.getInstant().getErrorCount() + 1);
            Config.getInstant().save();
            return true;
        }
        return false;
    }

    public boolean isSplitGenerate() {
        return splitGenerate;
    }

    public void setSplitGenerate(boolean splitGenerate) {
        this.splitGenerate = splitGenerate;
    }


    public void saveCurrentPackPath(String entityPackName) {
        if (entityPackName == null) {
            return;
        }
        setEntityPackName(entityPackName+".");
        save();
    }

}
