package editor;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.jreality.jogl.JOGLViewer;
import de.jreality.toolsystem.ToolSystem;

public class ProvaResult {

	public static void main(String[] args) {
	double [][]vertices={{-1.4208229280838445, 0.9336836384550977, -2.4999999999999964},
	{-1.3300012702366275, 0.887777904899678, -2.499999999999998},
	{-1.2412757443389426, 0.845244402680748, -2.4999999999999964},
	{-1.1544898895094335, 0.806076473888463, -2.4999999999999964},
	{-1.0694872448667416, 0.7702674606129778, -2.4999999999999956},
	{-0.9861113495295107, 0.7378107049444479, -2.4999999999999964},
	{-0.9042057426163829, 0.708699548973028, -2.4999999999999964},
	{-0.8236139632460001, 0.6829273347888728, -2.4999999999999964},
	{-0.7441795505370059, 0.6604874044821375, -2.4999999999999964},
	{-0.6657460436080429, 0.6413731001429774, -2.4999999999999964},
	{-0.5881569815777532, 0.6255777638615474, -2.4999999999999964},
	{-0.5112559035647796, 0.6130947377280023, -2.4999999999999964},
	{-0.43488634868776466, 0.603917363832497, -2.4999999999999964},
	{-0.3588918560653514, 0.5980389842651869, -2.4999999999999964},
	{-0.2831159648161821, 0.5954529411162269, -2.4999999999999964},
	{-0.20740221405889922, 0.5961525764757718, -2.4999999999999964},
	{-0.13159414291214572, 0.6001312324339766, -2.4999999999999964},
	{-0.05553529049456418, 0.6073822510809965, -2.4999999999999964},
	{0.020930804075202897, 0.6178989745069865, -2.4999999999999964},
	{0.09796060167851287, 0.6316747448021016, -2.4999999999999964},
	{0.17571056319672318, 0.6487029040564966, -2.4999999999999964},
	{0.2543371495111912, 0.6689767943603266, -2.4999999999999964},
	{0.33399682150327414, 0.6924897578037468, -2.4999999999999964},
	{0.41484604005432957, 0.7192351364769117, -2.4999999999999964},
	{0.49704126604571475, 0.7492062724699771, -2.4999999999999964},
	{0.5807389603587872, 0.7823965078730973, -2.4999999999999964},
	{0.6660955838749043, 0.8187991847764277, -2.4999999999999964},
	{0.7532675974754233, 0.858407645270123, -2.4999999999999964},
	{0.8424114620417016, 0.9012152314443385, -2.4999999999999964},
	{0.9336836384550965, 0.947215285389229, -2.4999999999999964},
	};
	
	double [][]vertices2={{-1.2178482240718669, 0.9291730894770537, -2.4999999999999964},
	{-1.2163879157735922, 0.9061671541706198, -2.4999999999999987},
	{-1.2065467121275537, 0.8538464823046965, -2.4999999999999964},
	{-1.1827128282678172, 0.7865239711098998, -2.4999999999999964},
	{-1.1422057893402937, 0.7137975981877664, -2.4999999999999956},
	{-1.0846977422794455, 0.6415460262642136, -2.4999999999999964},
	{-1.0116967551155995, 0.5727973915449861, -2.4999999999999964},
	{-0.9260875676899123, 0.5084790313627349, -2.4999999999999964},
	{-0.8317252576540015, 0.4480559068053612, -2.4999999999999964},
	{-0.7330772856312596, 0.3900654760152567, -2.4999999999999964},
	{-0.6349093834168777, 0.3325567738490826, -2.4999999999999964},
	{-0.5420107490936034, 0.2734414535877192, -2.4999999999999964},
	{-0.45895401294025123, 0.21076454638602493, -2.4999999999999964},
	{-0.3898854380099854, 0.14290269415203527, -2.4999999999999964},
	{-0.33834081925539816, 0.06869761154524286, -2.4999999999999964},
	{-0.3070825450774116, -0.012467467216407499, -2.4999999999999964},
	{-0.29795328517501624, -0.10064060105117353, -2.4999999999999964},
	{-0.31174176857287095, -0.19531545852018467, -2.4999999999999964},
	{-0.3480561157037897, -0.2954793824564874, -2.4999999999999964},
	{-0.40520018842313216, -0.3996874470268343, -2.4999999999999964},
	{-0.4800484218321228, -0.5061547515365772, -2.4999999999999964},
	{-0.5679146017871188, -0.6128591952880336, -2.4999999999999964},
	{-0.662410051971851, -0.7176469778026845, -2.4999999999999964},
	{-0.7552866944096558, -0.8183330687175739, -2.4999999999999964},
	{-0.8362604472927243, -0.9127888916662698, -2.4999999999999964},
	{-0.8928104240053873, -0.9990094664547526, -2.4999999999999964},
	{-0.9099493972184595, -1.0751522538425944, -2.4999999999999964},
	{-0.8699609919316635, -1.1395399472397982, -2.4999999999999964},
	{-0.7520990713411582, -1.190619455629648, -2.4999999999999964},
	{-0.5322447794091901, -1.2268693220279543, -2.4999999999999964},
	};
	
	
	Fibra f=new Fibra(vertices);
	Fibra f2=new Fibra(vertices2);
	
	ArrayList<Fibra>fibre=new ArrayList<Fibra>();
	fibre.add(f);
	fibre.add(f2);



	
	
	
	ResultPanel r=new ResultPanel(fibre);
	r.start();
	
		
	
	
	
	
	

}
}