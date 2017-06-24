package main;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import controller.BDKeyListener;
import controller.Controller;
import model.dao.DAOTest;
import view.Audio;
import view.CreateMenu;
import view.Gravity;
import view.LevelObservator;
import view.MapMaker;
import view.MonsterMove;
import view.TranslateMap;
import view.VictoryDiamonds;
import view.Window;
import view.move.Move;

public class Main implements LevelObservator {
	static MapMaker maker = null;
	static File music = null;
	static boolean test = true;
	final static int SET_SIZE = 16;
	static CreateMenu menu;

	public static void main(String[] args) throws IOException {
		menu = new CreateMenu();
		Main game = new Main();
		menu.getObservators().add(game);
	}

	/* (non-Javadoc)
	 * @see view.LevelObservator#onLevelSelected(int)
	 */
	
	@Override
	public void onLevelSelected(int level) {
		(new Thread(new Runnable() {
			@Override
			public void run() {
				DAOTest connectionBDD = new DAOTest();				
				connectionBDD.connection();
				connectionBDD.executeQuery(level);
				connectionBDD.setQueryIntoTable();
				
				try {
					connectionBDD.executeDiamondQuery(level);
					connectionBDD.setQueryDiamondsInToInteger();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				TranslateMap translate;
				
				try {
					
					translate = new TranslateMap(connectionBDD.getTab());
					maker = new MapMaker(translate);
					maker.spritesCreation(SET_SIZE);
					
					BDKeyListener bdkeyListener = new BDKeyListener();
					Window window = new Window(maker, bdkeyListener, connectionBDD.getFinalDiamonds(), level);
					Move move = new Move(maker.getSprites(), SET_SIZE, window.getPanel());
					Gravity gravity = new Gravity();
					MonsterMove monsterMove = new MonsterMove();
					VictoryDiamonds victoryDiamonds = new VictoryDiamonds();
					Audio clip = new Audio();
					Controller controller = new Controller(
							maker.getCharacter(translate.getCharacterX(), translate.getCharacterY()), window.getPanel(),
							SET_SIZE, move, maker, gravity, window, monsterMove, connectionBDD.getFinalDiamonds(),
							victoryDiamonds, clip);
					
					bdkeyListener.addObserver(controller);
					bdkeyListener.setController(controller);
					
				} catch (Exception e1) {
					e1.printStackTrace();
					
				}
			}
		})).start();
	}
}
