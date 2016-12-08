package com.royll.xmlformat;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiFile;
import com.royll.xmlformat.config.Config;
import com.royll.xmlformat.entity.ClassEntity;
import com.royll.xmlformat.entity.DataType;
import com.royll.xmlformat.entity.FieldEntity;
import com.royll.xmlformat.entity.IterableFieldEntity;
import com.royll.xmlformat.ui.FieldsDialog;
import com.royll.xmlformat.ui.Toast;
import com.royll.xmlformat.util.CheckUtil;
import com.royll.xmlformat.util.PsiClassUtil;
import com.royll.xmlformat.util.StringUtils;
import org.apache.http.util.TextUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by dim on 2015/8/21.
 * Edit by Roy on 2016/11/18
 * 把 Simplexml 转成 实体类
 */
public class ConvertBridge {

    private PsiClass targetClass;
    private PsiClass currentClass;
    private PsiElementFactory factory;
    private Project project;
    private PsiFile file;
    private String xmlStr;
    private HashMap<String, FieldEntity> declareFields;
    private HashMap<String, ClassEntity> declareClass;
    private String generateClassName;
    private ClassEntity generateClassEntity = new ClassEntity();
    private StringBuilder fullFilterRegex = null;
    private StringBuilder briefFilterRegex = null;
    private String filterRegex = null;
    private Operator operator;
    private String packageName;

    public ConvertBridge(Operator operator,
                         String xmlStr, PsiFile file, Project project,
                         PsiClass targetClass,
                         PsiClass currentClass, String generateClassName) {

        factory = JavaPsiFacade.getElementFactory(project);
        this.file = file;
        this.generateClassName = generateClassName;
        this.operator = operator;
        this.xmlStr = xmlStr;
        this.project = project;
        this.targetClass = targetClass;
        this.currentClass = currentClass;
        declareFields = new HashMap<>();
        declareClass = new HashMap<>();
        packageName = StringUtils.getPackage(generateClassName);
        fullFilterRegex = new StringBuilder();
        briefFilterRegex = new StringBuilder();
        CheckUtil.getInstant().cleanDeclareData();
        String[] arg = Config.getInstant().getAnnotationStr().replace("{filed}", "(\\w+)").split("\\.");

        for (int i = 0; i < arg.length; i++) {
            String s = arg[i];
            if (i == arg.length - 1) {
                briefFilterRegex.append(s);
                fullFilterRegex.append(s);
                Matcher matcher = Pattern.compile("\\w+").matcher(s);
                if (matcher.find()) {
                    filterRegex = matcher.group();
                }
            } else {
                fullFilterRegex.append(s).append("\\s*\\.\\s*");
            }
        }


    }

    public void run() {
        Document document = null;
        operator.cleanErrorInfo();
        try {
            document = parseXml(xmlStr);
        } catch (DocumentException e) {
            handleDataError(e);
        }
        if (document != null) {
            try {
//                ClassEntity classEntity = collectClassAttribute(targetClass, Config.getInstant().isReuseEntity());
                ClassEntity classEntity = null;
                if (classEntity != null) {
                    for (FieldEntity item : classEntity.getFields()) {
                        declareFields.put(item.getKey(), item);
                        CheckUtil.getInstant().addDeclareFieldName(item.getKey());
                    }
                }
                if (Config.getInstant().isSplitGenerate()) {
                    collectPackAllClassName();
                }
                operator.setVisible(false);
                parseDocument(document);
            } catch (Exception e2) {
                handleDataError(e2);
                e2.printStackTrace();
                operator.setVisible(true);
            }
        }
        declareFields = null;
        declareClass = null;
    }

    private Document parseXml(String xmlStr) throws DocumentException {
        SAXReader reader = new SAXReader();
        Document document = null;
        try {
            InputStream is = new ByteArrayInputStream(xmlStr.getBytes("UTF-8"));
            document = reader.read(is);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 获取该数组中所有标签到一个Element中
     *
     * @param eleArray
     * @return
     */
    private Element getFullElement(Element eleArray) {
        Iterator<Element> iterator = eleArray.elementIterator();
        List<Element> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        Element resElement = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            Iterator<Element> elements = list.get(i).elementIterator();
            while (elements.hasNext()) {
                Element element = elements.next();
                String name = element.getName();
                if (resElement.elements(name).size() == 0) {
                    Element newElement = resElement.addElement(name);
                    if (element.isTextOnly()) {
                        newElement.setText(element.getText());
                    }
                }
            }
        }
        return resElement;
    }

    private void collectPackAllClassName() {
        File packageFile = PsiClassUtil.getPackageFile(file, packageName);
        if (packageFile != null) {
            File[] files = packageFile.listFiles();
            if (files != null) {
                for (File file1 : files) {
                    if (packageName == null) {
                        CheckUtil.getInstant().addDeclareClassName(file1.getName());
                    } else {
                        CheckUtil.getInstant().addDeclareClassName(packageName + "." + file1.getName());
                    }
                }
            }
        }

    }

    private void handleDataError(Exception e2) {
        e2.printStackTrace();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e2.printStackTrace(printWriter);
        printWriter.close();
        operator.showError(Error.DATA_ERROR);
        operator.setErrorInfo(writer.toString());
    }


    /*private ClassEntity collectClassAttribute(PsiClass psiClass, boolean collectInnerClass) {
        if (psiClass == null) {
            return null;
        }
        ClassEntity innerClass = new ClassEntity();
        innerClass.setLock(true);
        declareClass.put(psiClass.getQualifiedName(), innerClass);
        CheckUtil.getInstant().addDeclareClassName(psiClass.getQualifiedName());
        innerClass.setClassName(psiClass.getName());
        innerClass.addAllFields(collectDeclareFields(psiClass));
        innerClass.setPsiClass(psiClass);
        innerClass.setPackName(getPackName(psiClass));
        if (collectInnerClass) {
            //recursionInnerClass(innerClass);
        }
        return innerClass;
    } */

/*    private void recursionInnerClass(ClassEntity classEntity) {
        PsiClass[] innerClassArray = classEntity.getPsiClass().getAllInnerClasses();
        for (PsiClass psiClass : innerClassArray) {
            ClassEntity item = new ClassEntity();
            item.setLock(true);
            declareClass.put(psiClass.getQualifiedName(), item);
            CheckUtil.getInstant().addDeclareClassName(psiClass.getQualifiedName());
            item.setClassName(psiClass.getName());
            item.addAllFields(collectDeclareFields(psiClass));
            item.setPsiClass(psiClass);
            item.setPackName(getPackName(psiClass));
            recursionInnerClass(item);
        }
    }*/

    public String getPackName(PsiClass psiClass) {
        String packName = null;
        if (psiClass.getQualifiedName() != null) {
            int i = psiClass.getQualifiedName().lastIndexOf(".");
            if (i >= 0) {
                packName = psiClass.getQualifiedName().substring(0, i);
            } else {
                packName = psiClass.getQualifiedName();
            }
        }
        return packName;

    }

    /**
     * 过滤掉// 和/** 注释
     *
     * @param str
     * @return
     */
    public String removeComment(String str) {
        String temp = str.replaceAll("/\\*" +
                "[\\S\\s]*?" +
                "\\*/", "");
        return temp.replaceAll("//[\\S\\s]*?\n", "");
    }

   /*private List<FieldEntity> collectDeclareFields(PsiClass mClass) {

        ArrayList<FieldEntity> filterFieldList = new ArrayList<>();
        if (mClass != null) {
            PsiField[] psiFields = mClass.getAllFields();
            for (PsiField psiField : psiFields) {
                String fileName = null;

                String psiFieldText = removeComment(psiField.getText());
                if (filterRegex != null && psiFieldText.contains(filterRegex)) {
                    boolean isSerializedName = false;
                    psiFieldText = psiFieldText.trim();
                    Pattern pattern = Pattern.compile(fullFilterRegex.toString());
                    Matcher matcher = pattern.matcher(psiFieldText);
                    if (matcher.find()) {
                        fileName = matcher.group(1);
                        isSerializedName = true;
                    }
                    pattern = Pattern.compile(briefFilterRegex.toString());
                    matcher = pattern.matcher(psiFieldText);
                    if (matcher.find()) {
                        fileName = matcher.group(1);
                        isSerializedName = true;
                    }
                    if (!isSerializedName) {
                        fileName = psiField.getName();
                    }
                } else {
                    fileName = psiField.getName();
                }
                FieldEntity fieldEntity = evalFieldEntity(null, psiField.getType());
                fieldEntity.setKey(fileName);
                fieldEntity.setFieldName(fileName);
                filterFieldList.add(fieldEntity);
            }
        }

        return filterFieldList;
    } */

    /*private FieldEntity evalFieldEntity(FieldEntity fieldEntity, PsiType type) {

        if (type instanceof PsiPrimitiveType) {
            if (fieldEntity == null) {
                fieldEntity = new FieldEntity();
            }
            fieldEntity.setType(type.getPresentableText());
            return fieldEntity;
        } else if (type instanceof PsiArrayType) {
            if (fieldEntity == null) {
                fieldEntity = new IterableFieldEntity();
            }
            IterableFieldEntity iterableFieldEntity = (IterableFieldEntity) fieldEntity;
            iterableFieldEntity.setDeep(iterableFieldEntity.getDeep() + 1);
            return evalFieldEntity(fieldEntity, ((PsiArrayType) type).getComponentType());
        } else if (type instanceof PsiClassReferenceType) {
            PsiClass psi = ((PsiClassReferenceType) type).resolveGenerics().getElement();

            if (isCollection(psi)) {
                if (fieldEntity == null) {
                    fieldEntity = new IterableFieldEntity();
                }
                IterableFieldEntity iterableFieldEntity = (IterableFieldEntity) fieldEntity;
                iterableFieldEntity.setDeep(iterableFieldEntity.getDeep() + 1);
                PsiType[] parameters = ((PsiClassReferenceType) type).getParameters();
                if (parameters.length > 0) {
                    PsiType parameter = parameters[0];
                    if (parameter instanceof PsiWildcardType) {
                        if (((PsiWildcardType) parameter).isExtends()) {
                            final PsiType extendsBound = ((PsiWildcardType) parameter).getExtendsBound();

                            evalFieldEntity(fieldEntity, extendsBound);
                        }
                        if (((PsiWildcardType) parameter).isSuper()) {
                            final PsiType superBound = ((PsiWildcardType) parameter).getSuperBound();
                            evalFieldEntity(fieldEntity, superBound);
                        }
                    } else if (parameter instanceof PsiClassReferenceType) {

                        PsiClass element = ((PsiClassReferenceType) parameter).resolveGenerics().getElement();
                        handleClassReferenceType(fieldEntity, element);
                    }
                }
                return fieldEntity;
            } else {

                if (fieldEntity == null) {
                    fieldEntity = new FieldEntity();
                }
                handleClassReferenceType(fieldEntity, psi);
                return fieldEntity;
            }

        }
        if (fieldEntity == null) {
            fieldEntity = new IterableFieldEntity();
        }
        return fieldEntity;
    } */

/*    private void handleClassReferenceType(FieldEntity fieldEntity, PsiClass psi) {
        if (psi == null || psi.getQualifiedName() == null) {
            return;
        }
        switch (psi.getQualifiedName()) {
            case "java.lang.String":
                fieldEntity.setType("String");
                break;
            case "java.lang.Boolean":
                fieldEntity.setType("Boolean");
                break;
            case "java.lang.Integer":
                fieldEntity.setType("Integer");
                break;
            case "java.lang.Double":
                fieldEntity.setType("Double");
                break;
            case "java.lang.Long":
                fieldEntity.setType("Long");
                break;
            default:
                ClassEntity classEntity = declareClass.get(psi.getQualifiedName());
                if (classEntity == null) {
                    classEntity = collectClassAttribute(psi, true);
                }
                fieldEntity.setTargetClass(classEntity);
                break;
        }
    }*/

    private boolean isCollection(PsiClass element) {

        if ("java.util.Collection".equals(element.getQualifiedName())) {
            return true;
        }
        for (PsiClass psiClass : element.getInterfaces()) {
            if (isCollection(psiClass)) {
                return true;
            }
        }
        return false;
    }

    private void parseDocument(Document document) {
        Map<String, Integer> generateFiled = collectGenerateFiled(document);
        if (generateFiled.size() > 1) {
            //对象中不能含有多组相同的标签 如：
            /**<students>
             <student>
             <name>a</name>
             <age>23</age>
             <sex>man</sex>
             </student>
             <student>
             <name>b</name>
             <age>22</age>
             <sex>woman</sex>
             </student>
             <total>2</total>
             </students>*/
            for (Map.Entry<String, Integer> entry : generateFiled.entrySet()) {
                if (entry.getValue() > 1) {
                    operator.setVisible(true);
                    operator.showError(Error.DATA_ERROR, "对象中不能含有多组相同的标签:" + entry.getKey());
                    return;
                }
            }
        }
        List<String> generateFiledList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : generateFiled.entrySet()) {
            generateFiledList.add(entry.getKey());
        }
        if (Config.getInstant().isVirgoMode()) {
            handleVirgoMode(document, generateFiledList);
        } else {
            // handleNormal(json, generateFiled);
        }
    }

    private void handleVirgoMode(Document document, List<String> fieldList) {
        generateClassEntity.setClassName("");
        generateClassEntity.setPsiClass(targetClass);
        generateClassEntity.addAllFields(createFields(document.getRootElement(), fieldList, generateClassEntity));
        FieldsDialog fieldsDialog = new FieldsDialog(operator, generateClassEntity, factory,
                targetClass, currentClass, file, project, generateClassName);
        fieldsDialog.setSize(800, 500);
        fieldsDialog.setLocationRelativeTo(null);
        fieldsDialog.setVisible(true);
    }

/*    private void handleNormal(JSONObject json, List<String> generateFiled) {
        WriteCommandAction.runWriteCommandAction(project, new Runnable() {
            @Override
            public void run() {
                if (targetClass == null) {
                    try {
                        targetClass = PsiClassUtil.getPsiClass(file, project, generateClassName);
                    } catch (Throwable throwable) {
                        handlePathError(throwable);
                    }
                }
                if (targetClass != null) {
                    generateClassEntity.setPsiClass(targetClass);
                    try {
                        generateClassEntity.addAllFields(createFields(json, generateFiled, generateClassEntity));
                        operator.setVisible(false);
                        DataWriter dataWriter = new DataWriter(file, project, targetClass);
                        dataWriter.execute(generateClassEntity);
                        Config.getInstant().saveCurrentPackPath(packageName);
                        operator.dispose();
                    } catch (Exception e) {
                        throw e;
                    }
                }
            }
        });
    }*/

    private Map<String, Integer> collectGenerateFiled(Document document) {
        //保存根标签，需要注解到class name上面
        Config.getInstant().setRootElementName(document.getRootElement().getName());
        Iterator<Element> elementIterator = document.getRootElement().elementIterator();
        Map<String, Integer> fieldMap = new HashMap<>();
        addDeclareField(elementIterator, fieldMap);
        return fieldMap;
    }

    private void addDeclareField(Iterator<Element> elementIterator, Map<String, Integer> fieldMap) {
        while (elementIterator.hasNext()) {
            Element element = elementIterator.next();
            String name = element.getName();
            System.out.println("element name :" + name);
            System.out.println("element count :" + element.nodeCount());
            if (!existDeclareField(name, element)) {
                if (fieldMap.containsKey(name)) {
                    int count = fieldMap.get(name) + 1;
                    fieldMap.put(name, count);
                } else {
                    fieldMap.put(name, 1);
                }
            }

        }
    }

    private void addDeclareField(Iterator<Element> elementIterator, List<String> fieldList) {
        while (elementIterator.hasNext()) {
            Element element = elementIterator.next();
            String name = element.getName();
            System.out.println("element name :" + name);
            System.out.println("element count :" + element.nodeCount());
            if (!existDeclareField(name, element)) {
                fieldList.add(name);
            }
        }
    }

    private boolean existDeclareField(String key, Node node) {
        FieldEntity fieldEntity = declareFields.get(key);
        if (fieldEntity == null) {
            return false;
        }
        return fieldEntity.isSameType(node);
    }

    private void handlePathError(Throwable throwable) {
        throwable.printStackTrace();
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        operator.setErrorInfo(writer.toString());
        operator.setVisible(true);
        operator.showError(Error.PATH_ERROR);
    }

    private List<FieldEntity> createFields(Element element, List<String> fieldList, ClassEntity parentClass) {

        List<FieldEntity> fieldEntityList = new ArrayList<>();
        List<String> listEntityList = new ArrayList<>();
        boolean writeExtra = Config.getInstant().isGenerateComments();


        for (int i = 0; i < fieldList.size(); i++) {
            String key = fieldList.get(i);
            String childKey;
            Element keyElement = element.element(key);

            if (keyElement.elementIterator().hasNext()) {
                childKey = keyElement.elementIterator().next().getName();
                List<Element> elements = keyElement.elements(childKey);
                if (elements != null) {
                    if (elements.size() > 1) {
                        //当做数组处理
                        listEntityList.add(key);
                        continue;
                    } else if (elements.size() == 1 && keyElement.nodeCount() == 3) {
                        //只有一个元素当成数组
/*                      <CERTIFICDETALS>
                            <CERTIFICDETAL>
                                <CERTIFICTYPE>0</CERTIFICTYPE>
                                <CERTIFICSTATUS>02</CERTIFICSTATUS>
                                <FREEZESTATUS>01</FREEZESTATUS>
                            </CERTIFICDETAL>
                        </CERTIFICDETALS>*/
                        //当做数组处理
                        listEntityList.add(key);
                        continue;
                    } else {
                        System.out.println("key = " + key);
                    }
                }
            }
            FieldEntity fieldEntity = createField(parentClass, key, element.element(key), false);
            fieldEntityList.add(fieldEntity);

//            if (writeExtra) {
//                writeExtra = false;
//                parentClass.setExtra(Utils.createCommentString(json, fieldList));
//            }
        }
        for (int i = 0; i < listEntityList.size(); i++) {
            String key = listEntityList.get(i);
            FieldEntity fieldEntity = createField(parentClass, key, element, true);
            fieldEntityList.add(fieldEntity);
        }

        return fieldEntityList;
    }


    private FieldEntity createField(ClassEntity parentClass, String key, Element type, boolean isArray) {
        //过滤 不符合规则的key
        String fieldName = CheckUtil.getInstant().handleArg(key);
        if (Config.getInstant().isUseSerializedName()) {
            fieldName = StringUtils.captureStringLeaveUnderscore(convertSerializedName(fieldName));
        }
        fieldName = handleDeclareFieldName(fieldName, "");

        FieldEntity fieldEntity = typeByValue(parentClass, key, type, isArray);
        if (fieldEntity != null) {
            fieldEntity.setFieldName(fieldName);
        }
        return fieldEntity;
    }

    private String convertSerializedName(String fieldName) {
        if (Config.getInstant().isUseFieldNamePrefix() &&
                !TextUtils.isEmpty(Config.getInstant().getFiledNamePreFixStr())) {
            fieldName = Config.getInstant().getFiledNamePreFixStr() + "_" + fieldName;
        }
        return fieldName;
    }

    private FieldEntity typeByValue(ClassEntity parentClass, String key, Element element, boolean isArray) {
        FieldEntity result = null;

        if (isArray) {
            result = handleXmlArray(parentClass, element, key, 1);
        } else {
            if (element.isTextOnly()) {
                FieldEntity fieldEntity = new FieldEntity();
                fieldEntity.setKey(key);
                fieldEntity.setType(DataType.typeOfObject(element.getTextTrim()).getValue());
                result = fieldEntity;
                if (element != null) {
                    result.setValue(element.getTextTrim());
                }
            } else {
//            ClassEntity classEntity = existDeclareClass((JSONObject) type);
                ClassEntity classEntity = null;
                if (classEntity == null) {
                    FieldEntity fieldEntity = new FieldEntity();
                    ClassEntity innerClassEntity = createInnerClass(createSubClassName(key, element), element, parentClass);
                    innerClassEntity.setKey(key);
                    fieldEntity.setKey(key);
                    fieldEntity.setTargetClass(innerClassEntity);
                    result = fieldEntity;
                } else {
                    FieldEntity fieldEntity = new FieldEntity();
                    fieldEntity.setKey(key);
                    fieldEntity.setTargetClass(classEntity);
                    result = fieldEntity;
                }
            }
        }
        result.setKey(key);
        return result;
    }

/*    private ClassEntity existDeclareClass(JSONObject jsonObject) {
        for (ClassEntity classEntity : declareClass.values()) {
            Iterator<String> keys = jsonObject.keys();
            boolean had = false;
            while (keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObject.get(key);
                had = false;
                for (FieldEntity fieldEntity : classEntity.getFields()) {
                    if (fieldEntity.getKey().equals(key) && DataType.isSameDataType(DataType.typeOfString(fieldEntity.getType()), DataType.typeOfObject(value))) {
                        had = true;
                        break;
                    }
                }
                if (!had) {
                    break;
                }
            }
            if (had) {
                return classEntity;
            }
        }
        return null;
    }*/

    /**
     * @param
     * @param
     * @param
     * @return
     */
    private ClassEntity createInnerClass(String className, Element element, ClassEntity parentClass) {

/*        if (Config.getInstant().isSplitGenerate()) {
            String qualifiedName = packageName == null ? className : packageName + "." + className;
            if (CheckUtil.getInstant().containsDeclareClassName(qualifiedName)) {
                //存在同名。
                PsiClass psiClass = PsiClassUtil.exist(file, qualifiedName);
                if (psiClass != null) {
                    ClassEntity classEntity = collectClassAttribute(psiClass, false);
                    classEntity.setLock(true);
                    if (classEntity.isSame(json)) {
//                        if (Config.getInstant().isReuseEntity()) {
                        declareClass.put(classEntity.getQualifiedName(), classEntity);
//                        }
                        return classEntity;
                    }
                }
            }
        } */

        ClassEntity subClassEntity = new ClassEntity();

        Iterator<Element> elementIterator = element.elementIterator();
        List<String> fieldList = new ArrayList<>();
        addDeclareField(elementIterator, fieldList);
        List<FieldEntity> fields = createFields(element, fieldList, subClassEntity);
        subClassEntity.addAllFields(fields);
        if (Config.getInstant().isSplitGenerate()) {
            subClassEntity.setPackName(packageName);
        } else {
            subClassEntity.setPackName(parentClass.getQualifiedName());
        }
        subClassEntity.setClassName(className);
        if (handleDeclareClassName(subClassEntity, "")) {
            CheckUtil.getInstant().addDeclareClassName(subClassEntity.getQualifiedName());
        }
        if (Config.getInstant().isReuseEntity()) {
            declareClass.put(subClassEntity.getQualifiedName(), subClassEntity);
        }
        parentClass.addInnerClass(subClassEntity);

        return subClassEntity;
    }

    private boolean handleDeclareClassName(ClassEntity classEntity, String appendName) {

        classEntity.setClassName(classEntity.getClassName() + appendName);
        if (CheckUtil.getInstant().containsDeclareClassName(classEntity.getQualifiedName())) {
            return handleDeclareClassName(classEntity, "X");
        }
        return true;
    }

    private String handleDeclareFieldName(String fieldName, String appendName) {
        fieldName += appendName;
        if (CheckUtil.getInstant().containsDeclareFieldName(fieldName)) {
            return handleDeclareFieldName(fieldName, "X");
        }
        return fieldName;
    }

    private String createSubClassName(String key, Object o) {
        String name = "";
        if (TextUtils.isEmpty(key)) {
            return key;
        }
        String[] strings = key.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            stringBuilder.append(StringUtils.captureName(strings[i]));
        }

        name = stringBuilder.toString() + Config.getInstant().getSuffixStr();

        return name;

    }

    /**
     * @param parentClass
     * @param element     rootElement
     * @param key         数组标签名
     * @param deep        层级
     * @return
     */
    private FieldEntity handleXmlArray(ClassEntity parentClass, Element element, String key, int deep) {

        FieldEntity fieldEntity;
        if (element.nodeCount() > 1) {
            Element item = getFullElement(element.element(key));
            fieldEntity = listTypeByValue(parentClass, element.element(key).getName(), item, deep);
        } else {
            fieldEntity = new IterableFieldEntity();
            fieldEntity.setKey(key);
            fieldEntity.setType("?");
            ((IterableFieldEntity) fieldEntity).setDeep(deep);
        }
        return fieldEntity;
    }

    private FieldEntity listTypeByValue(ClassEntity parentClass, String key, Element element, int deep) {
        FieldEntity result = null;
        if (element.isTextOnly()) {
            FieldEntity fieldEntity = new FieldEntity();
            fieldEntity.setKey(key);
            fieldEntity.setType(DataType.typeOfObject(element.getTextTrim()).getValue());
            result = fieldEntity;
            if (element != null) {
                result.setValue(element.getTextTrim());
            }
        } else {
//            ClassEntity classEntity = existDeclareClass((JSONObject) type);
            ClassEntity classEntity = null;
            if (classEntity == null) {
                IterableFieldEntity iterableFieldEntity = new IterableFieldEntity();
                ClassEntity innerClassEntity = createInnerClass(createSubClassName(element.getName(), element), element, parentClass);
                innerClassEntity.setKey(element.getName());
                iterableFieldEntity.setKey(key);
                iterableFieldEntity.setDeep(deep);
                iterableFieldEntity.setTargetClass(innerClassEntity);
                result = iterableFieldEntity;
            } else {
                IterableFieldEntity fieldEntity = new IterableFieldEntity();
                fieldEntity.setKey(key);
                fieldEntity.setTargetClass(classEntity);
                fieldEntity.setType(classEntity.getQualifiedName());
                fieldEntity.setDeep(deep);
                result = fieldEntity;
            }
        }
        result.setKey(key);
        return result;
    }


    public interface Operator {

        void showError(Error err);

        void showError(Error err, String str);

        void dispose();

        void setVisible(boolean visible);

        void setErrorInfo(String error);

        void cleanErrorInfo();
    }

    public enum Error {
        DATA_ERROR, PARSE_ERROR, PATH_ERROR;
    }
}

