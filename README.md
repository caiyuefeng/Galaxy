# Galaxy
## 数据处理
### 模块:Sun           Hadoop 功能框架
<list>
  <li>自定义分区实现</li>
  <li>断点工作流</li>
  <li>新旧数据快速分拣功能</li>
</list>

### 模块:Asteroid  通用工具类

### 模块:Saturn    分布式数据传输处理工具

### 模块:Jupiter   单机Graph图库



### 模块:Sirius    线程同步框架

- 静态模式
使用Sync注解标识同步方法，并且静态调用Sirius启动类,该方法会让同步方法一直在后台运行直到同步状态解除

- 动态模式
使用Sync注解调用标识同步方法，并在调用该方法时自动将方法使用线程运行
ASM字节码技术替换原类文件，

无损替换方式
1、根启动类，先生成同步类字节码文件，再使用自定义类加载器加载

2、 加载同步类字节码文件，不加载原类字节码


## 1 部署使用说明

### 1.1 目录结构说明
 \-${部署根路径}

 \--bin     *(启动脚本、操作脚本、工具脚本路径)*

 \---<font color=#743A3A>${模块名称}.sh</font>

 \--conf        *(配置文件路径)*

 \---<font color=#EA7500>galaxy.properties</font>       *(Galaxy 通用键值对配置文件)*

 \---<font color=#EA7500>galaxy-${模块名称}-site.xml</font>     *(Galaxy 模块/子项目 Xml格式配置文件)*

 \---<font color=#EA7500>log4j.properties</font>        *(Log4j 日志配置文件)*

 \--lib     *(依赖包路径)*

 \---<font color=#64A600>galaxy-${模块名}-${版本号}.jar</font>

 \---<font color=#64A600>${其他依赖包}.jar</font>

 \--<font color=#EA7500>globe.properties</font>     *(安装时配置文件)*

 \--<font color=#743A3A>install.sh</font>       *(安装脚本)*

 \--<font color=#743A3A>check.sh</font>     *(检测脚本)*




































