package roseindia.net.advice;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SimpleAdvice implements MethodInterceptor {

	@Override
	public Object invoke(MethodInvocation methodInvocation) throws Throwable {
		// TODO Auto-generated method stub
		System.out.println("Proceed " + methodInvocation.getMethod().getName()
				+ "()");
		Object object = methodInvocation.proceed();
		System.out.println("Method End");
		return object;
	}

}
