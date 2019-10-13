package synthesijer.tools.xilinx;

import java.io.File;
import java.util.Enumeration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import synthesijer.hdl.HDLPort;
import synthesijer.hdl.HDLSignalBinding;

public class GenComponentXML {

	private Document document;

	public final String VENDOR_NAME;
	public final String LIBRARY_NAME;
	public final String CORE_NAME;
	public final PortInfo[] ports;
	public final String[] files;
	public final HDLSignalBinding[] bindings;

	public final int MajorVersion;
	public final int MinorVersion;

	public GenComponentXML(String vendor, String lib, String core, int major, int minor, PortInfo[] ports, String[] files, HDLSignalBinding[] bindings) {
		this.VENDOR_NAME = vendor;
		this.LIBRARY_NAME = lib;
		this.CORE_NAME = core;
		this.MajorVersion = major;
		this.MinorVersion = minor;
		this.ports = ports;
		this.files = files;
		this.bindings = bindings;

		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			document = documentBuilder.newDocument();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		Element root = genRoot();
		document.appendChild(root);
		root.appendChild(genTextNode("spirit:vendor", VENDOR_NAME));
		root.appendChild(genTextNode("spirit:library", LIBRARY_NAME));
		root.appendChild(genTextNode("spirit:name", CORE_NAME));
		root.appendChild(genTextNode("spirit:version", MajorVersion + "." + MinorVersion));
		if(bindings.length > 0){
			root.appendChild(genBusInterfaces());
		}
		if(hasAddressSpaces()){
			root.appendChild(genAddressSpaces());
		}
		if(hasMemoryMaps()){
			root.appendChild(genMemoryMaps());
		}
		root.appendChild(genModel());
		root.appendChild(genFileSets());
		root.appendChild(genTextNode("spirit:description", getCoreUniqName()));
		root.appendChild(genGlobalParameters());
		root.appendChild(genVendorExtensions());
	}

	private boolean hasAddressSpaces(){
		for(HDLSignalBinding b: bindings){
			if(b.hasAddressSpace()) return true;
		}
		return false;
	}

	private boolean hasMemoryMaps(){
		for(HDLSignalBinding b: bindings){
			if(b.hasMemoryMap()) return true;
		}
		return false;
	}

	public String getCoreUniqName(){
		return CORE_NAME + "_" + "v" + MajorVersion + "_" + MinorVersion;
	}

	private Element genRoot(){
		Element element = document.createElement("spirit:component");
		element.setAttribute("xmlns:xilinx", "http://www.xilinx.com");
		element.setAttribute("xmlns:spirit", "http://www.spiritconsortium.org/XMLSchema/SPIRIT/1685-2009");
		element.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		return element;
	}

	private Element genTextNode(String label, String value){
		Element element = document.createElement(label);
		element.appendChild(document.createTextNode(value));
		return element;
	}

	private Element genTextNode(String label, String value, ParameterPair[] list){
		Element element = document.createElement(label);
		element.appendChild(document.createTextNode(value));
		for(ParameterPair pp: list){
			element.setAttribute(pp.key, pp.value);
		}
		return element;
	}

	public Element genBusInterfaces(){
		Element element = document.createElement("spirit:busInterfaces");
		for(HDLSignalBinding b: bindings){
			Element e0 = document.createElement("spirit:busInterface");
			element.appendChild(e0);
			e0.appendChild(genTextNode("spirit:name", b.getName()));
			e0.appendChild(
					genTextNode("spirit:busType", "",
							new ParameterPair[]{
									new ParameterPair("spirit:vendor", b.getVendor()),
									new ParameterPair("spirit:library", b.getLibrary()),
									new ParameterPair("spirit:name", b.getBusTypeName()),
									new ParameterPair("spirit:version", b.getVersion()),
							}));
			e0.appendChild(
					genTextNode("spirit:abstractionType", "",
							new ParameterPair[]{
									new ParameterPair("spirit:vendor", b.getVendor()),
									new ParameterPair("spirit:library", b.getLibrary()),
									new ParameterPair("spirit:name", b.getBusAbstractionTypeName()),
									new ParameterPair("spirit:version", b.getVersion()),
							}));
			if(b.isMaster()){
				Element e1 = document.createElement("spirit:master");
				e0.appendChild(e1);
				if(b.hasAddressSpace()){
					e1.appendChild(genTextNode("spirit:addressSpaceRef", "",
							new ParameterPair[]{ new ParameterPair("spirit:addressSpaceRef", b.getName()) }));
				}
			}else{
				Element e1 = document.createElement("spirit:slave");
				e0.appendChild(e1);
				if(b.hasMemoryMap()){
					e1.appendChild(genTextNode("spirit:memoryMapRef", "",
							new ParameterPair[]{ new ParameterPair("spirit:memoryMapRef", b.getName()) }));
				}
			}
			e0.appendChild(genPortMaps(b));
		}
		return element;
	}

	private Element genPortMaps(HDLSignalBinding b){
		Element element = document.createElement("spirit:portMaps");
		Enumeration<HDLPort> keys = b.getKeys();
		while(keys.hasMoreElements()){
			HDLPort p = keys.nextElement();
			String logicalName = b.get(p);
			Element e0 = document.createElement("spirit:portMap");
			element.appendChild(e0);
			{
				Element e1 = document.createElement("spirit:logicalPort");
				e0.appendChild(e1);
				e1.appendChild(genTextNode("spirit:name", logicalName));
			}
			{
				Element e1 = document.createElement("spirit:physicalPort");
				e0.appendChild(e1);
				e1.appendChild(genTextNode("spirit:name", p.getName()));
			}
		}
		return element;
	}

	private Element genAddressSpaces(){
		Element element = document.createElement("spirit:addressSpaces");
		for(HDLSignalBinding b: bindings){
			if(b.hasAddressSpace() == false) continue;
			Element e0 = document.createElement("spirit:addressSpace");
			element.appendChild(e0);
			e0.appendChild(genTextNode("spirit:name", b.getName()));
			e0.appendChild(genTextNode("spirit:range", String.valueOf(b.getRange()), //"4294967296",
					new ParameterPair[]{
							new ParameterPair("spirit:format", "long"),
							new ParameterPair("spirit:resolve", "user")
					}));
			e0.appendChild(genTextNode("spirit:width", "32",
					new ParameterPair[]{
							new ParameterPair("spirit:format", "long"),
							new ParameterPair("spirit:resolve", "user")
					}));
		}
		return element;
	}

	private Element genMemoryMaps(){
		Element element = document.createElement("spirit:memoryMaps");
		for(HDLSignalBinding b: bindings){
			if(b.hasMemoryMap() == false) continue;
			Element e0 = document.createElement("spirit:memoryMap");
			element.appendChild(e0);
			e0.appendChild(genTextNode("spirit:name", b.getName()));
			Element e1 = document.createElement("spirit:addressBlock");
			e0.appendChild(e1);
			{
				e1.appendChild(genTextNode("spirit:name", b.getAddressBlockName()));
				e1.appendChild(genTextNode("spirit:baseAddress", "0",
						new ParameterPair[]{
								new ParameterPair("spirit:format", "bitString"),
								new ParameterPair("spirit:resolve", "user"),
								new ParameterPair("spirit:bitStringLength", "32"),
						}));
				e1.appendChild(genTextNode("spirit:range", String.valueOf(b.getRange()), //"4294967296",
						new ParameterPair[]{
								new ParameterPair("spirit:format", "long"),
								new ParameterPair("spirit:resolve", "user")
						}));
				e1.appendChild(genTextNode("spirit:width", "32",
						new ParameterPair[]{
								new ParameterPair("spirit:format", "long"),
								new ParameterPair("spirit:resolve", "user")
						}));
			}
		}
		return element;
	}

	private Element genModel(){
		Element element = document.createElement("spirit:model");
		element.appendChild(genViews());
		element.appendChild(genPorts());
		return element;
	}

	private Element genViews(){
		Element element = document.createElement("spirit:views");
		element.appendChild(genView("xilinx_vhdlsynthesis", "VHDL Synthesis", "vhdlSource:vivado.xilinx.com:synthesis", "vhdl", CORE_NAME, "xilinx_vhdlsynthesis_view_fileset"));
		element.appendChild(genView("xilinx_vhdlbehavioralsimulation", "VHDL Simulation", "vhdlSource:vivado.xilinx.com:simulation", "vhdl", CORE_NAME, "xilinx_vhdlbehavioralsimulation_view_fileset"));
		element.appendChild(genView("xilinx_xpgui", "UI Layout", ":vivado.xilinx.com:xgui.ui", "xilinx_xpgui_view_fileset"));
		return element;
	}

	private Element genView(String name, String displayName, String envId, String lang, String modelName, String fileSetLabel){
		Element element = document.createElement("spirit:view");
		element.appendChild(genTextNode("spirit:name", name));
		element.appendChild(genTextNode("spirit:displayName", displayName));
		element.appendChild(genTextNode("spirit:envIdentifier", envId));
		element.appendChild(genTextNode("spirit:language", lang));
		element.appendChild(genTextNode("spirit:modelName", modelName));
		element.appendChild(genFileSetRef(fileSetLabel));
		return element;
	}

	private Element genView(String name, String displayName, String envId, String fileSetLabel){
		Element element = document.createElement("spirit:view");
		element.appendChild(genTextNode("spirit:name", name));
		element.appendChild(genTextNode("spirit:displayName", displayName));
		element.appendChild(genTextNode("spirit:envIdentifier", envId));
		element.appendChild(genFileSetRef(fileSetLabel));
		return element;
	}

	private Element genFileSetRef(String label){
		Element element = document.createElement("spirit:fileSetRef");
		element.appendChild(genTextNode("spirit:localName", label));
		return element;
	}

	class ParameterPair{
		public final String key, value;
		public ParameterPair(String k, String v){
			this.key = k;
			this.value = v;
		}
	}

	private Element genParameters(ParameterPair[] list){
		Element element = document.createElement("spirit:parameters");
		for(ParameterPair pp: list){
			Element e = document.createElement("parameter");
			e.appendChild(genTextNode("spirit:name", pp.key));
			e.appendChild(genTextNode("spirit:value", pp.key));
			element.appendChild(e);
		}
		return element;
	}

	private Element genPorts(){
		Element element = document.createElement("spirit:ports");
		for(PortInfo info: ports){
			element.appendChild(genPort(info));
		}
		return element;
	}

	private Element genPort(PortInfo info){
		Element element = document.createElement("spirit:port");
		element.appendChild(genTextNode("spirit:name", info.name));
		{
			Element e0 = document.createElement("spirit:wire");
			element.appendChild(e0);
			e0.appendChild(genTextNode("spirit:direction", info.dir));
			if(info.vector){
				Element e1 = document.createElement("spirit:vector");
				e1.appendChild(genTextNode("spirit:left", String.valueOf(info.left), new ParameterPair[]{new ParameterPair("spirit:format", "long"), new ParameterPair("spirit:resolve", "immediate")}));
				e1.appendChild(genTextNode("spirit:right", String.valueOf(info.right), new ParameterPair[]{new ParameterPair("spirit:format", "long"), new ParameterPair("spirit:resolve", "immediate")}));
				e0.appendChild(e1);
			}
			{
				Element e1 = document.createElement("spirit:wireTypeDefs");
				e0.appendChild(e1);
				{
					Element e2 = document.createElement("spirit:wireTypeDef");
					e1.appendChild(e2);
					e2.appendChild(genTextNode("spirit:typeName", info.type));
					e2.appendChild(genTextNode("spirit:viewNameRef", "xilinx_vhdlsynthesis"));
					e2.appendChild(genTextNode("spirit:viewNameRef", "xilinx_vhdlbehavioralsimulation"));
				}
			}
		}
		return element;
	}

	private Element genFileSets(){
		Element element = document.createElement("spirit:fileSets");
		String gui = "xgui/" + getCoreUniqName() + ".tcl";
		element.appendChild(genSrcFileSet("xilinx_vhdlsynthesis_view_fileset", files));
		element.appendChild(genSrcFileSet("xilinx_vhdlbehavioralsimulation_view_fileset", files));
		element.appendChild(genXpguiFileSet("xilinx_xpgui_view_fileset", gui));
		return element;
	}

	private Element genSrcFileSet(String name, String[] list){
		Element element = document.createElement("spirit:fileSet");
		element.appendChild(genTextNode("spirit:name", name));
		for(String s: list){
			Element e0 = document.createElement("spirit:file");
			e0.appendChild(genTextNode("spirit:name", s));
			e0.appendChild(genTextNode("spirit:fileType", "vhdlSource"));
			e0.appendChild(genTextNode("spirit:isIncludeFile", "false"));
			e0.appendChild(genTextNode("spirit:logicalName", getCoreUniqName())); // "xil_defaultlib"));
			element.appendChild(e0);
		}
		return element;
	}

	private Element genXpguiFileSet(String name, String src){
		Element element = document.createElement("spirit:fileSet");
		element.appendChild(genTextNode("spirit:name", name));
		Element e0 = document.createElement("spirit:file");
		e0.appendChild(genTextNode("spirit:name", src));
		e0.appendChild(genTextNode("spirit:userFileType", "XGUI_VERSION_2"));
		e0.appendChild(genTextNode("spirit:fileType", "tclSource"));
		e0.appendChild(genTextNode("spirit:isIncludeFile", "false"));
		element.appendChild(e0);
		return element;
	}

	public Element genGlobalParameters(){
		Element element = document.createElement("spirit:parameters");
		Element e0 = document.createElement("spirit:parameter");
		e0.appendChild(genTextNode("spirit:name", "Component_Name"));
		e0.appendChild(genTextNode("spirit:value", getCoreUniqName(), new ParameterPair[]{ new ParameterPair("spirit:resolve", "user"), new ParameterPair("spirit:id", "PARAM_VALUE.Component_Name"), new ParameterPair("spirit:order", "1")}));
		element.appendChild(e0);
		return element;
	}

	public Element genVendorExtensions(){
		Element element = document.createElement("spirit:vendorExtensions");
		{
			Element core = document.createElement("xilinx:coreExtensions");
			core.appendChild(genSupportedFamilies());
			core.appendChild(genTaxonomies());
			core.appendChild(genTextNode("xilinx:displayName", getCoreUniqName()));
			core.appendChild(genTextNode("xilinx:coreRevision", "1"));
			core.appendChild(genTextNode("xilinx:coreCreationDateTime", "2015-03-03T01:26:40Z"));
			element.appendChild(core);
		}
		element.appendChild(genPackageInfo());
		return element;
	}

	public Element genSupportedFamilies(){
		Element element = document.createElement("xilinx:supportedFamilies");
		String[] families = new String[]{ "virtex7",
				"qvirtex7",
				"kintex7",
				"kintex7l",
				"qkintex7",
				"qkintex7l",
				"artix7",
				"artix7l",
				"aartix7",
				"qartix7",
				"zynq",
				"qzynq",
				"azynq",
				"virtexu",
				"kintexu"
		};
		for(String f: families){
			element.appendChild(genTextNode("xilinx:family", f, new ParameterPair[]{ new ParameterPair("xilinx:lifeCycle", "Production") }));
		}

		return element;
	}

	public Element genTaxonomies(){
		Element element = document.createElement("xilinx:taxonomies");
		element.appendChild(genTextNode("xilinx:taxonomy", "/BaseIP"));
		return element;
	}

	public Element genPackageInfo(){
		Element element = document.createElement("xilinx:packagingInfo");
		element.appendChild(genTextNode("xilinx:xilinxVersion", "2014.1"));
		return element;
	}

	public boolean write(File file) {

		// generate Transformer intance
		Transformer transformer = null;
		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			transformer = transformerFactory.newTransformer();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			return false;
		}

		// Transformer configuration
		transformer.setOutputProperty("indent", "yes"); // new line
		transformer.setOutputProperty("encoding", "UTF-8"); // encoding

		// generate XML file
		try {
			transformer.transform(new DOMSource(document), new StreamResult(
					file));
		} catch (TransformerException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		PortInfo[] ports = new PortInfo[]{
				new PortInfo("clk", "in", "std_logic"),
				new PortInfo("reset", "in", "std_logic"),
				new PortInfo("q", "out", "std_logic") };
		String[] files = new String[]{"test.vhd", "top.vhd"};

		GenComponentXML o = new GenComponentXML("vendor", "user", "test", 1, 0, ports, files, new HDLSignalBinding[0]);
		// generate XML file
		File file = new File("component.xml");
		o.write(file);

	}

}
