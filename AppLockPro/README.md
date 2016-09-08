**出于安全考虑，我没有放上签名文件。所以如果您用Android Studio重新编译，会提示找不到签名文件，编译失败。请自行生成签名文件。且由于签名发生变化，部分第三方SDK功能将无法正常运行。如需正常运行，请见下方说明**

**注意，上述各SDK均要求注册申请APPID。如需要，请自行至相应平台注册申请，然后修改utils/Configuration中的相应配置文件。当前源码中的APPID是我自己申请的不保证以后仍然可用！**

Android应用锁 - App保护的锁屏软件
===================================================
功能介绍
---------------------------------------------------
这个App是我在自学Android时写的第1个正式完整的App，历时大概1个半月。   
  
其主要功能是监控用户当前将要打开的App，与内部数据库存储的需要保护的App列表比较，一旦发现使用的App需要保护，会立即弹出一个密码输入界面，只有输入正确的密码才能进入到该App使用界面中！    

好吧，其实类似的软件已经很多了！但是这对我来说，确实是一个比较容易实践的工程。   

很多应用市场发布的应用锁只有简单锁定功能，相比之下，这个App的锁定模式和条件要更高级些。

###主要功能###
* 支持多种配置：用户可添加多种锁定配置，并在需要的时候随时切换；
* 支持特定条件下的锁定：可选择特定时间、特定地点上锁定；
* 支持异常访问监控：在监控有异常访问时，可后台拍照，并纪录到日志当中。  

###设计原则###
考虑到自己主要是为了学习Android而写的该App，所以代码的编写更多的是从学习的角度去考虑。例如：  

* 第三方社会化分享没有使用友盟/ShareSDK之类的组件，而是直接使用的官方的SDK；
* 尽可能的多用一些新特性，如RecyleView、CardView、SwipeRefreshLayout、FloatingActionBar等；
* 用了一些设计模式的东西 ，主要是创建类型的模式；
* 添加一些看似无用的功能，如用户登陆、手机号码注册和验证、用户反馈功能。此部分功能实质没有太大用处，主要是补上与服务器通信的功能；
* 集成第三方组件，如百度地图、个推、BMOB。一来是这些自己无力/时间/金钱去设计，二来也是想学习如何快速地集成现有的组件；三来这些也是我所知的开发过程中比较常用的东西。
* 只发布以了腾讯的应用宝市场，因为不打算长期维护这个App，所以只是用来熟悉这个发布流程。

## 本项目使用的第三方组件
* BMOB：后端云服务商，官方网站http://bmob.cn/。  使用了其服务端数据库/用户登陆/手机号码验证功能。  
* 个推：提供消息推送功能，官方网站：https://dev.getui.com。  使用消息推送机制用于通知用户的反馈建议有了回复。  
* 百度地图：提供实时定位和地图检查功能，官方网站：http://lbsyun.baidu.com/。 使用其定位功能检查是否处理需要锁屏的位置，地图功能用于选取锁屏位置。  
* 微信分享SDK：提供分享到微信的功能，官方网站：https://open.weixin.qq.com/。  使用其将App的信息发享至微信。
* QQ分享SDK：提供分享到QQ的功能，官方网站：http://open.qq.com/。    使用其将App的信息分享对QQ。


###不足之处###
总体而言，我对这个App还是比较满意的，像个样子。但是仍有一些不足之处，是我单靠自学看书解决速度较慢的。  

* 布局优化和性能优化：这一块基本没有做；
* 功能还不够完善：解锁还存在漏洞，未添加防卸载功能，代码还存在一些Bug；

##联系我
博客: http://eeontheway.com

## 主要功能截图欣赏
###启动界面
第一次打开软件时，会要求设置初始密码，该密码将会用于后来的解锁。设置完成后将进入主界面。  
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-105628.png)  
###主界面
主界面分三块，配置需要锁定的App、什么情况下锁定、监控到的异常访问纪录。  
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-113411.png)  
### APP配置
该配置可列出系统中所有的已安装的App列表，然后用户可任意选择多个App，添加到配置列表中。  
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110244.png)  
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110212.png)  
###锁定条件
锁定条件支持两种类型，基于特定时间和基于特定地理位置。只有当所有的锁定条件均满足时，才可执行App锁功能。这样便可用于多种场景，例如：可能希望在工作日/在公司里，才锁定App，其它时间地点不锁定。   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110327.png)   
时间类型的锁定，可选择起始时间/结束时间/星期。      
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110257.png)   
位置类型的锁定，可在地图上选择需要位置。由于定位存在偏差，所以在检查锁定时，会检查一定的范围，只要在该范围内，都进行锁定操作，有效见图中的粉红色区域。  
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110320.png)   
###访问纪录
在发现有人输入密码达到指定次数时，App会在后台控制摄像头拍照，将纪录到日志列表中。  
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110735.png)   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110744.png)   
###多模式支持
支持创建多种锁定配置，可随时切换到指定配置。  
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-114722.png)   
###应用配置
该界面主要配置一些APP相关的运行参数。  
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110500.png)   
###关于界面
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110541.png)   
###用户反馈
提供一个用户反馈建议入口。用户在此填与反馈信息后   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110554.png)   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110602.png)   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110608.png)   
开发人员一旦回复，用户将会收到推送消息，告知有消息回复。   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-163548.png)   

###应用锁定
当发现有需要锁定的App时，且锁定条件满足，后台服务会弹出锁屏窗口。   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110658.png)   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110722.png)   

### 第三方社会化分享
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110348.png)   
### 用户注册与登陆
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110419.png)   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110427.png)   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-110437.png)   
### 在线升级
启动时会检查升级信息，判断是否要升级。也可手动强制升级。   
![image](https://github.com/tongban/AppLocker/raw/master/app/screenshot/device-2016-03-14-112932.png)   

当然，还可以在这个软件继续做其它功能，如本地数据库同步到远程数据库，更灵活的锁定模式等待。有兴趣话，欢迎fork
