package com.remedy.apibase;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.runner.RunWith;


import com.remedy.apibase.config.RemedyConfigTest;
import com.remedy.apibase.controller.APIBaseControllerTest;
import com.remedy.apibase.controller.TestControllerTest;
import com.remedy.apibase.service.APIBaseServiceTest;
import com.remedy.apibase.service.RemedyServiceTest;
import com.remedy.apibase.service.RequestServiceTest;


@RunWith(JUnitPlatform.class)
@SelectClasses({
	APIBaseControllerTest.class,
	TestControllerTest.class,
	APIBaseServiceTest.class,
	RemedyServiceTest.class,
	RequestServiceTest.class,
	RemedyConfigTest.class
})
public class UnitTestSuite {

}