# simplexml-format

### 介绍
simplexml-format是一个Intellij IDEA和Android Studio中的代码生成工具类插件，利用它可以很方便地将XML格式的String转换成[SimpleXml](http://simple.sourceforge.net/)支持的Java实体类，其代码是在开源项目[GsonForamt](https://github.com/zzz40500/GsonFormat)的基础上编写。

### 安装
File->Settings..->Plugins-->Browse repositores..搜索simplexml-format,重启Android Studio/Intellij IDEA。
或者：
在[这里](https://plugins.jetbrains.com/plugin/9327?pr=idea)下载zip文件，打开File->Settings..->Plugins -->install plugin from disk..导入文件并重启Android Studio/Intellij IDEA。

### 使用
* 打开Generate菜单（Android Studio快捷键alt+Insert），点击xmlformat选项
![](https://github.com/lololiu/simplexml-format/raw/master/imgs/generate.png)

* 直接快捷键alt+1打开

### 截图

![默认生成](https://github.com/lololiu/simplexml-format/raw/master/imgs/screenshot1.gif)

![自定义变量名](https://github.com/lololiu/simplexml-format/raw/master/imgs/screenshot2.gif)

### 感谢
* [GsonForamt](https://github.com/zzz40500/GsonFormat)

### License

    Copyright 2014 The simplexml-format Authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.