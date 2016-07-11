package org.lemsml.jlems.test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.Test;
import org.lemsml.jlems.core.expression.ParseError;
import org.lemsml.jlems.core.flatten.ComponentFlattener;
import org.lemsml.jlems.core.logging.E;
import org.lemsml.jlems.core.run.ConnectionError;
import org.lemsml.jlems.core.run.RuntimeError;
import org.lemsml.jlems.core.sim.ContentError;
import org.lemsml.jlems.core.sim.ParseException;
import org.lemsml.jlems.core.sim.Sim;
import org.lemsml.jlems.core.type.BuildException;
import org.lemsml.jlems.core.type.Component;
import org.lemsml.jlems.core.type.ComponentType;
import org.lemsml.jlems.core.type.Lems;
import org.lemsml.jlems.core.xml.XMLException;
import org.lemsml.jlems.io.logging.DefaultLogger;
import org.lemsml.jlems.io.reader.FileInclusionReader;
import org.lemsml.jlems.io.xmlio.XMLSerializer;

public class FlatRunTest {

    public static void main(String[] args) throws ContentError, ParseError, ConnectionError, RuntimeError, IOException, ParseException, BuildException, XMLException {
        DefaultLogger.initialize();

        FlatRunTest cft = new FlatRunTest();
        cft.runExample1();

    }

    @Test
    public void runExample1() throws ContentError, ConnectionError, ParseError, IOException, RuntimeError, ParseException, BuildException, XMLException {
        URL url = this.getClass().getResource("/example1.xml");
        File f1 = new File(url.getFile());
        flattenFromFile(f1, "na");
    }

    public void flattenFromFile(File f, String tgtid) throws ContentError,
            ConnectionError, ParseError, IOException, RuntimeError, ParseException,
            BuildException, XMLException {
        E.info("Loading LEMS file from: " + f.getAbsolutePath());

        FileInclusionReader fir = new FileInclusionReader(f);
        Sim sim = new Sim(fir.read());

        sim.readModel();

        sim.build();

        //sim.run();
        Lems lems = sim.getLems();
        Component cpt = lems.getComponent(tgtid);

        ComponentFlattener cf = new ComponentFlattener(lems, cpt, true, true);

        ComponentType ct = cf.getFlatType();
        Component cp = cf.getFlatComponent();

        String typeOut0 = XMLSerializer.serialize(cpt.getComponentType());
        String typeOut = XMLSerializer.serialize(ct);
        String cptOut = XMLSerializer.serialize(cp);

        E.info("Flat type before: \n" + typeOut0);
        E.info("Flat type after: \n" + typeOut);
        E.info("Flat cpt: \n" + cptOut);

        lems.addComponentType(ct);
        lems.addComponent(cp);

        lems.resolve(ct);
        lems.resolve(cp);

        lems.setTargetComponent(cp);
        sim.build();

        E.info("running simulation...");
        sim.run();
        E.info("Done");
    }
}
