/**
 * The package to store the event that represent a command.
 * 
 * <p>该包用来存放命令类型的事件。
 * 
 * <p>The command event is used for passing asynchronous commands between aggregates, between
 * microservices, or from user interface to core area. These events are not independently determined
 * by one model, or they are defined by the application, so put them in the application layer.
 * 
 * <p>命令事件用于在聚合之间、在微服务之间，或从用户界面到核心区域传递异步命令。这些事件不是由单个模型独立决定的，或者它们就是由应用程序定义的，所以放在应用程序层。
 * 
 * <p>Note: <ul><li>In addition to sending commands to the application, the user interface can also
 * call application service directly. Which way to use, and whether it is synchronous or
 * asynchronous, just depend on the actual business case. <li>User interface will subscribe regular
 * domain event when reverse call is needed. <li>For event between microservices, whether it is a
 * regular event or a command event, the subscriber usually does not define a Class/Object to accept
 * it, but directly reads it through the media reader. </ul>
 * 
 * <p>注意：<ul><li>除了向应用程序发送命令外，用户接口还可以直接调用应用程序。使用哪种方式，以及是同步还是异步，取决于实际的业务场景。<li>用户接口将在需要反向调用时订阅常规域事件。
 * * <li>对于微服务之间的事件，无论是常规事件还是命令事件，订阅者通常不定义一个类/对象来接受它，而是通过媒体读取器直接读取它。</ul>
 */
package xyz.hnlxl.dddim.application.command;
