package ssg.serverlessblog.autotests;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import ssg.serverlessblog.daobase.PageComponentLogic;
import ssg.serverlessblog.documentref.PageComponentDoc;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.SampleDataUtil;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(OrderAnnotation.class)
public class TestPageComponentLogic {

	@BeforeAll
	static void checkEnvironment() {
		TestingUtil.checkReqEnvironment();
	}
	
	static String pageComponentId = "";
	
	@Test
	@Order(1)
	void createComp() {
		try {
			ObjectMapper mapper = new ObjectMapper();			
			pageComponentId = PageComponentLogic.createPageComponent(AppConst.PC_TYPE_TEXT_BOX, 
					mapper.writeValueAsString(SampleDataUtil.getSampleTextBox()), 999, false);
			
			assertFalse(pageComponentId.equals(""));
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error");
		}
	}
	
	@Test
	void updateComp() {
		try {
			var pc = PageComponentLogic.getPageComponent(pageComponentId);
			
			assertTrue(pc.isPresent());
			
			PageComponentLogic.updatePageComponent(pageComponentId, pc.get().getString(PageComponentDoc.field_json), 998, false);
			
			pc = PageComponentLogic.getPageComponent(pageComponentId);
			
			assertTrue(pc.get().getLong(PageComponentDoc.field_view_order) == 998L);
			
		} catch (Exception e) {
			e.printStackTrace();
			fail("Error");
		}
	}
	
	@AfterAll
	static void cleanUp() {
		try {
			if(PageComponentLogic.deletePageComponent(pageComponentId)) {
				System.out.println("Deleted pageComponent: %s".formatted(pageComponentId));
			}else {
				fail("Failed to delete pageComponent: %s".formatted(pageComponentId));
			}
		}catch(Exception e) {
			e.printStackTrace();
			fail("Failed to delete pageComponent: %s".formatted(pageComponentId));
		}
		return;
	}
}
