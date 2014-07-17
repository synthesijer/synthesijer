public class Test005{
	Test004 obj0 = new Test004();
	Test004 obj1 = new Test004();
	Test004 obj2 = new Test004();

	public void run(){
		obj0.start();
		obj1.start();
		obj2.start();
		try{
			obj0.join();
			obj1.join();
			obj2.join();
		}catch(Exception e){
		}
	}
}
