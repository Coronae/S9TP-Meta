public class Main {

	
	public static void main(String[] args) {
		Sac s = new Sac();
		s.lectureFichier("100Md5_1_1_pos.txt");

		//test algo
		s.algoGlouton();
		System.out.print(s);
		//test solution
		s.sommeSac();
		//test ecriture
		s.ecrireFichier("sortie.txt", "100Md5_1_1_pos.txt");
	}
}
