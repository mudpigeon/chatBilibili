package top.nino.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.Arrays;


@SuppressWarnings("all")
public class SchedulingRunnableUtil implements Runnable {
	
	private static final Logger LOGGER  = LogManager.getLogger(SchedulingRunnableUtil.class);
	private String beanName;

    private String methodName;

    private Object[] params;
    
    
    public SchedulingRunnableUtil(String beanName, String methodName) {
        this(beanName, methodName, new  Object[]{});
    }

    public SchedulingRunnableUtil(String beanName, String methodName, Object...params ) {
        this.beanName = beanName;
        this.methodName = methodName;
        this.params = params;
    }
    
    @Override
    public void run() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        SchedulingRunnableUtil that = (SchedulingRunnableUtil) o;
        if (this.params == null) {
            return this.beanName.equals(that.beanName) &&
                    this.methodName.equals(that.methodName) &&
                    that.params == null;
        }

        return this.beanName.equals(that.beanName) &&
                this.methodName.equals(that.methodName) &&
                Arrays.equals(this.params,that.params);
    }



    @Override
    public int hashCode() {
//        if (this.params == null||this.params.length<=0) {
//            return Objects.hash(this.beanName, this.methodName);
//        }
//        return Objects.hash(this.beanName, this.methodName,  Arrays.hashCode(this.params));
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getBeanName() == null) ? 0 : getBeanName().hashCode());
        result = prime * result + ((getMethodName() == null) ? 0 : getMethodName().hashCode());
        result = prime * result + (((this.params == null) ? 0 :Arrays.hashCode(this.params)));
        return result;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
