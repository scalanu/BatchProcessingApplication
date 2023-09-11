package com.example.batchprocessing;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

public class CustomRetryListener implements RetryListener {
	
	Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void close(RetryContext retryContext, RetryCallback retryCallback, Throwable throwable) {
	}

	@Override
	public void onError(RetryContext retryContext, RetryCallback retryCallback, Throwable throwable) {
		logger.debug("Retry Count: " + retryContext.getRetryCount());
		if(retryContext.getRetryCount() == 3) {
			logger.error("Retry Count reached max allowed with error : /n"+retryContext.getLastThrowable().getMessage());
		}
		try {
			retryCallback.doWithRetry(retryContext);
		} catch (Throwable e) {
			logger.error("retry fallback encountered error : \n"+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public boolean open(RetryContext retryContext, RetryCallback retryCallback) {
		return true;
	}
}
