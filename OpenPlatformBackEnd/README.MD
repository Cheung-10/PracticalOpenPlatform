备注说明
-----
####1.路径规范
后端转换路径时不会调用path.join方法，因此所有路径应当自带首端间隔符，例如：\
`path = "\test_model"`\
推荐用反斜杠（windows和linux均兼容）

####2.数据库表备注
1) user表中，id由系统生成，username指用户学号或工号（只能是纯数字），密码可为空。
2) runtime_environment表，表项为指定k8s运行脚本需要的环境，
流程为pod先加载对应imageBase，然后在从nfs挂载点读取脚本文件并启动，
注意目前没有对新添加运行模板的runtime_environment_id进行检查的步骤。
3) template表是保存process或者train任务的程序参数模板的表，与其他多个表有外键关联，
而task是保存启动任务编号的表，taskId为字符串，长度上限为255。
最后，task_log为保存运行完成信息的表。

####3.k8s调用备注
1) 调度原理为创建k8s-job，然后将job推送到client。之后将watchJob方法以Handler形式启动。
2) 创建job前，会将XXXTemplate转换为jobConfig。
3) 涉及文件路径时使用`getMountPath`方法转换为nfs服务器路径（而不是使用`FileAndPathUtil`）。
4) 涉及的参数将以Key-Value的形式添加到启动脚本的命令行中，应当使用args.parse来收集参数。
参数一定包括"testInput"、"testOutput"，同时包括由模板runtime_parameter指定的参数，例如：
```
用环境"python"启动脚本"test.py"，参数为"time"，参数值为"10",那么相当于最后启动脚本的命令为：
python xx\test.py --input xx --output xx --times 10
```
因此，所有部署到后端的脚本开头应当包括argument.parse部分来收集参数,例如需要搜集times参数：
```
parser = argparse.ArgumentParser(description='PyTorch MNIST Example')
parser.add_argument('--input', default="./" ,help='path to dataset')
parser.add_argument('--output', default="./" ,help='path to evaluate sample')
parser.add_argument('--times', default=10,
                    help='folder to output images and model checkpoints')

args = parser.parse_args()
# convert value from argument to variable
times = (int)args.time
```
注意：--input 和 --onput 来自XXXTemplate中的PathParameter字段的name属性。
因为设置inputPathParameterTemplate和outputPathParameter非空，所以该参数一定会被解析（即使脚本中并不需要）。
当然，不需要时设置不予收集即可。

后端测试
------
1) 要启动后端，需要提供以下环境：\
a.数据库（必要，远程连接获取）\
b.文件空间（必要，一般通过nfs挂载）\
c.RabbitMQ（不必要，但是没有时后端会持续尝试连接）\
d.k8s服务（必要，远程连接获取）\

2) 另外框架中得某些功能在进行核心功能测试时不必要，可以设置暂时关闭，包括：\
a.spring-security\
b.获取用户名\
c.获取用户角色及判断用户是否为管理员\

详细设置如下：

####1.spring-security
测试基本功能时，可关闭security框架。\
暂时关闭SpringSecurity的方法：\
`1. 在SpringBootApplication中添加exclude = { SecurityAutoConfiguration.class }`\
`2. 将SpringSecurity包中SecurityConfig文件中的@Configuration、@EnableWebSecurity注解 注释掉`

####2.1获取用户名
在用户登入后获得JWT颁发的令牌，此后会话时会携带此令牌。\
在涉及用户空间（PRIVATE）的文件操作时，将通过`SecurityContextHolder`解析用户名。\
但在测试调度功能时这样太过麻烦，若希望使用固定用户名，可以在`util.UserUtil.java`中修改`getUsername`方法。\
将方法中的前两行注释掉，并反注释最后一行，结果如下：
```
    public static  String getUsername(){
//      String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//      return username;
        return "测试用的用户名";
    }
```
####2.2判断是否为管理员
这在FileSpaceController的接口中大量用到，目的是确认文件空间访问权限。\
`将UserUtil.java`中的`isAdmin`方法修改。\
注释原来的代码并直接返回true（测试时使用admin）。\
```
    public static boolean isAdmin(){
//        List<String> userRole=getRole();
//        if(userRole.contains("ROLE_ADMIN")){
//            return true;
//        }
//        return false;
        return true;
    }
```
####3.rabbitMQ
修改`config.RabbitMQConfig`，将@config注释掉：\
`//@Configuration`\
由于在kubernetes相关类中使用了rabbitMQ-Template，所以仍然会自动尝试刷新消息队列，不过仅会产生warn。

####4.nfs挂载
从nfs服务器挂载到本地，并修改application.yml中的`open-platform.filepath.base`到挂载点即可。

####5.mysql设置
使用远程账号连接mysql服务,参数见文档.

####6.k8s设置
可能需要设置的东西：
1) 环境
2) 镜像
3) 文件挂载点

参考项目链接：\
`https://github.com/usualheart/install_k8s_official`
