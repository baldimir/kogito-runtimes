package org.drools.kproject;

import org.junit.Test;
import org.kie.builder.KieBaseModel;
import org.kie.builder.KieFactory;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieProjectModel;
import org.kie.builder.KieSessionModel;
import org.kie.builder.ListenerModel;
import org.kie.builder.QualifierModel;
import org.kie.builder.WorkItemHandelerModel;
import org.kie.conf.AssertBehaviorOption;
import org.kie.conf.EventProcessingOption;
import org.kie.runtime.conf.ClockTypeOption;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.drools.kproject.KieProjectModelImpl.fromXML;
import static org.junit.Assert.assertNull;

public class KieProjectTest {

    @Test
    public void testMarshallingUnmarshalling() {
        KieFactory kf = KieFactory.Factory.get();

        KieProjectModel kproj = kf.newKieProject();

        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1")
                .setEqualsBehavior( AssertBehaviorOption.EQUALITY )
                .setEventProcessingMode( EventProcessingOption.STREAM );

        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1")
                .setType("stateful")
                .setClockType( ClockTypeOption.get("realtime") );

        ksession1.newListenerModel("org.domain.FirstInterface");

        ksession1.newListenerModel("org.domain.SecondInterface")
                .newQualifierModel("MyQualfier2");

        ksession1.newListenerModel("org.domain.ThirdInterface")
                .newQualifierModel("MyQualfier3")
                .setValue("v1");

        ksession1.newListenerModel("org.domain.FourthInterface")
                .newQualifierModel("MyQualfier4")
                .addArgument("name1", "xxxx")
                .addArgument("name2", "yyyy");

        ksession1.newWorkItemHandelerModel("org.domain.FifthInterface")
                .newQualifierModel("MyQualfier5")
                .addArgument("name1", "aaa")
                .addArgument("name2", "bbb");

        String xml = kproj.toXML();

        KieProjectModel kprojXml = fromXML(xml);

        KieBaseModel kieBaseModelXML = kprojXml.getKieBaseModels().get("KBase1");
        assertEquals(AssertBehaviorOption.EQUALITY, kieBaseModelXML.getEqualsBehavior());
        assertEquals(EventProcessingOption.STREAM, kieBaseModelXML.getEventProcessingMode());

        KieSessionModel kieSessionModelXML = kieBaseModelXML.getKieSessionModels().get("KSession1");
        assertEquals("stateful", kieSessionModelXML.getType());
        assertEquals(ClockTypeOption.get("realtime"), kieSessionModelXML.getClockType());

        List<ListenerModel> listeners = kieSessionModelXML.getListenerModels();

        ListenerModel listener1 = listeners.get(0);
        assertEquals("org.domain.FirstInterface", listener1.getType());
        assertNull(listener1.getQualifierModel());

        ListenerModel listener2 = listeners.get(1);
        assertEquals("org.domain.SecondInterface", listener2.getType());
        QualifierModel qualifier2 = listener2.getQualifierModel();
        assertEquals("MyQualfier2", qualifier2.getType());

        ListenerModel listener3 = listeners.get(2);
        assertEquals("org.domain.ThirdInterface", listener3.getType());
        QualifierModel qualifier3 = listener3.getQualifierModel();
        assertEquals("MyQualfier3", qualifier3.getType());
        assertEquals("v1", qualifier3.getValue());

        ListenerModel listener4 = listeners.get(3);
        assertEquals("org.domain.FourthInterface", listener4.getType());
        QualifierModel qualifier4 = listener4.getQualifierModel();
        assertEquals("MyQualfier4", qualifier4.getType());
        assertEquals("xxxx", qualifier4.getArguments().get("name1"));
        assertEquals("yyyy", qualifier4.getArguments().get("name2"));

        WorkItemHandelerModel wihm = kieSessionModelXML.getWorkItemHandelerModels().get(0);
        assertEquals("org.domain.FifthInterface", wihm.getType());
        QualifierModel qualifier5 = wihm.getQualifierModel();
        assertEquals("MyQualfier5", qualifier5.getType());
        assertEquals("aaa", qualifier5.getArguments().get("name1"));
        assertEquals("bbb", qualifier5.getArguments().get("name2"));

    }
}
