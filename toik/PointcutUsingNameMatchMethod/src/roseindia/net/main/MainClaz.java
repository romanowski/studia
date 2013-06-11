package roseindia.net.main;

import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import roseindia.net.advice.SimpleAdvice;
import roseindia.net.bean.SimpleBean;

public class MainClaz {
	public static void main(String[] args) {
		SimpleBean simpleBean = new SimpleBean();
		NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
		nameMatchMethodPointcut.addMethodName("sayHi");
		nameMatchMethodPointcut.addMethodName("greet");
		SimpleAdvice simpleAdviceadvice = new SimpleAdvice();
		Advisor advisor = new DefaultPointcutAdvisor(nameMatchMethodPointcut,
				simpleAdviceadvice);

		ProxyFactory proxyFactory = new ProxyFactory();
		proxyFactory.addAdvisor(advisor);
		proxyFactory.setTarget(simpleBean);

		SimpleBean proxyBean = (SimpleBean) proxyFactory.getProxy();

		proxyBean.sayHi("Vinay");
		proxyBean.sayHi();
		proxyBean.greet();

	}
}
