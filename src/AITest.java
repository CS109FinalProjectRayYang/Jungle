import structures.Game;
import structures.players.CP_ForeSighted;
import structures.players.CP_ShortSighted;

public class AITest {
    public static void main(String[] args) {
        int countBlue = 0, countRed = 0, countDraw = 0;
        for (int i = 0; i < 100; i++) {
            Game game = new Game(new CP_ForeSighted(), new CP_ShortSighted());
            game.withOutClient();
            game.start();
            if (game.getGameCondition() == 1) {
                countBlue++;
            } else if (game.getGameCondition() == -1) {
                countRed++;
            } else {
                countDraw++;
            }
        }
        System.out.println(countBlue);
        System.out.println(countRed);
        System.out.println(countDraw);
    }
}
