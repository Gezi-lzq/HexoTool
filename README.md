# HexoTool

方便博客发布的小工具

* java8
* javafx



### 软件界面

![image-20210920133953698](https://gitee.com/gezilzq/image-host/raw/master/img/image-20210920133953698.png)

* 下方点击`Edit`选择你的本地博客文件夹
  * 需要注意，你的博客markdown文件是否是放在`\source\_posts`这个文件夹内的
* 将任意路径下写好的博客文章（mardown文件）拖入到上方方框内
* 修改文件名为博客标题
* 添加分类与标签
* 点击发布
  * 软件会自动把博客文章复制到`\source\_posts`文件夹下，并添加头部`yaml`博客信息
  * 软件会自动执行`hexo g`,`hexo d`命令  （如果要是执行`hexo clean`自己手动执行，写入进去每次都clean，会运行太慢）

