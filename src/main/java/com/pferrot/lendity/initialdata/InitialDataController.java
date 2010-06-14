package com.pferrot.lendity.initialdata;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


public class InitialDataController extends AbstractController
{
	
	private InitialData initialData;
	
	public void setInitialData(InitialData initialData) {
		this.initialData = initialData;
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception
	{
		Exception exception = null;
		try {
			initialData.createAll();
		}
		catch (Exception e) {
			exception = e;
		}
		
		ModelAndView mv = new ModelAndView();
		mv.addObject("exception", exception);
		mv.setViewName("initialdata");		

		return mv;
	}	
}
