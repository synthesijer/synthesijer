public class SieveSim implements Runnable{
        
        Sieve p = new Sieve();
        
        public void run(){
                p.perform(512);
        }

}

