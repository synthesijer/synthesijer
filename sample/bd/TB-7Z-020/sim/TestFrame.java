

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TestFrame extends JFrame{
	
	TestCanvas canvas;
	public TestFrame(){
		canvas = new TestCanvas();
		getContentPane().add(canvas);
		pack();
		setVisible(true);
	}
	
	private byte[] buffer = new byte[1920*1080*3];
	
	public void pset(int x, int y, int rgb){
		System.out.printf("%d, %d => %d\n", x, y, 3*(x+y*1920)+2);
		buffer[3*(x+y*1920)+0] = (byte)((rgb >>  0) & 0x000000FF);
		buffer[3*(x+y*1920)+1] = (byte)((rgb >>  8) & 0x000000FF);
		buffer[3*(x+y*1920)+2] = (byte)((rgb >> 16) & 0x000000FF);
	}
	
	public void fill_rect(int x, int y, int w, int h, int rgb){
		for(int i = 0; i < w; i++){
			for(int j = 0; j < h; j++){
				pset(x+i, y+j, rgb);
			}
		}
	}
	
	public void flush(){
		canvas.repaint();
	}
	
	class TestCanvas extends JPanel{
		private BufferedImage img = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
		
		TestCanvas(){
			setSize(1920, 1080);
			setPreferredSize(new Dimension(1920, 1080));
		}
		
		public void paint(Graphics g){
			super.paint(g);
			for(int i = 0; i < 1920; i++){
				for(int j = 0; j < 1080; j++){
					int r0 = buffer[3*(i+j*1920)+2] & 0x000000FF;
					int g0 = buffer[3*(i+j*1920)+1] & 0x000000FF;
					int b0 = buffer[3*(i+j*1920)+0] & 0x000000FF;
					img.setRGB(i, j, (r0 << 16) + (g0 << 8) + b0);
				}
			}
			g.drawImage(img, 0, 0, this);
		}
	}
		
	public static void main(String[] args){
		TestFrame canvas = new TestFrame();
		System.out.println("fefe");
		canvas.fill_rect(0, 0, 100, 100, 0x00FF0000);
		System.out.println("fefe");
		canvas.flush();
		System.out.println("fefe");
	}

}
