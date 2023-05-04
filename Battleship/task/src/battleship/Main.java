package battleship;

import java.io.IOException;
import java.lang.Math;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        BattleField battleField1 = new BattleField();
        BattleField battleField2 = new BattleField();
        Player player1 = new Player(battleField1, "Player 1");
        Player player2 = new Player(battleField2, "Player 2");

        System.out.println("Player 1, place your ships on the game field");
        placeFleet(battleField1, player1.getFleet());

        switchPlayers();

        System.out.println("Player 2, place your ships on the game field");
        placeFleet(battleField2, player2.getFleet());

        switchPlayers();

        placeAshot(player1, player2);
    }

    private static void switchPlayers () {
        int inChar;
        System.out.println("Press Enter and pass the move to another player");
        try {
            inChar = System.in.read();
            while (inChar != 10) {
                inChar = System.in.read();
                System.out.println(inChar);
            }
            System.out.println("...");
        }
        catch (IOException e){
            System.out.println("Error reading from user");
        }
    }
    private static void placeAshot(Player player1, Player player2) {
        Player active = player1;
        Player passive = player2;
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        String input;
        System.out.println();
        boolean stop = false;
        while (!stop) {
            passive.getBattleField().setFogOfWarOn(true);
            passive.getBattleField().drawBattleField();
            System.out.println("---------------------");
            active.getBattleField().setFogOfWarOn(false);
            active.getBattleField().drawBattleField();
            System.out.println();
            System.out.println(active.getPlayerName() + ", it's your turn:");
            System.out.println();


            input = scanner.nextLine();
            System.out.println();
            try {
                Coordinates coords = parser.getDecodedCoordinate(input.toUpperCase());
                if (passive.getBattleField().isHit(coords)) {
                    passive.getBattleField().drawBattleField();
                    AbstractBattleship ship = passive.getBattleField().getBattleShipByCoords(coords);
                    if (!ship.isAfloat(passive.getBattleField())) {
                        if (passive.getBattleField().isAnyoneAfloat()) {
                            System.out.println("You sank a ship!");
                            switchPlayers();
                        } else {
                            System.out.println("You sank the last ship. You won. Congratulations!");
                            stop = true;
                        }
                    } else {
                        System.out.println("You hit a ship!");
                        switchPlayers();
                    }
                } else {
                    passive.getBattleField().drawBattleField();
                    System.out.println("You missed!");
                    switchPlayers();
                }
                active  =  active.equals(player1) ? player2 : player1;
                passive = passive.equals(player1) ? player2 : player1;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage() + " Try again:");
            }
        }
    }
    private static void placeFleet(BattleField battleField, AbstractBattleship[] fleet) {
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        String input;
        for (int i = 0; i < fleet.length; i++) {
            battleField.drawBattleField();
            System.out.println("Enter the coordinates of the " + fleet[i].getBattleshipType() + " (" + fleet[i].length + " cells)");
            System.out.println();
            boolean stop = false;
            while (!stop) {
                try {
                    input = scanner.nextLine();
                    System.out.println();
                    Coordinates[] shipCoords = parser.parseCoordinates(input.toUpperCase());
                    if (shipCoords[0].getX() == shipCoords[1].getX() &&
                        shipCoords[0].getY() > shipCoords[1].getY() ||
                        shipCoords[0].getY() == shipCoords[1].getY() &&
                        shipCoords[0].getX() > shipCoords[1].getX()) {
                        fleet[i].setCoordinates(shipCoords[1], shipCoords[0]);
                    } else {
                        fleet[i].setCoordinates(shipCoords[0], shipCoords[1]);
                    }
                    battleField.addBattleShip(fleet[i]);
                    stop = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage() + " Try again:");
                    System.out.println();
                }
            }
        }
        battleField.drawBattleField();
    }
}

class Parser {

    public Coordinates getDecodedCoordinate(String strCoord) {
        String coordReg = "([A-J])([0-9]{1,2})";
        Pattern pattern = Pattern.compile(coordReg, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(strCoord);
        if (matcher.find()) {
            int x = Integer.parseInt(matcher.group(2)) - 1;
            int y = 0;
            switch (matcher.group(1)) {
                case "A": y = 0; break;
                case "B": y = 1; break;
                case "C": y = 2; break;
                case "D": y = 3; break;
                case "E": y = 4; break;
                case "F": y = 5; break;
                case "G": y = 6; break;
                case "H": y = 7; break;
                case "I": y = 8; break;
                case "J": y = 9; break;
            }
            return new Coordinates(x, y);
        } else {
            throw new IllegalArgumentException("Error! Wrong coordinates!");
        }
    }
    public Coordinates[] parseCoordinates(String input) {
        String characterReg = "([A-J][0-9]{1,2})\\s([A-J][0-9]{1,2})";
        Pattern pattern = Pattern.compile(characterReg, Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            Coordinates[] coordinates = new Coordinates[2];
            String[] strCoords = matcher.group(0).split(" ");
            coordinates[0] = getDecodedCoordinate(strCoords[0]);
            coordinates[1] = getDecodedCoordinate(strCoords[1]);
            return coordinates;
        } else {
            throw new IllegalArgumentException("Error! Wrong coordinates!");
        }
    }
}
class Coordinates {
    private int x;
    private int y;

    public Coordinates() {
        x = 0;
        y = 0;
    }
    public Coordinates(int x, int y) {
        setX(x);
        setY(y);
    }
    public int getX() {
        return x;
    }

    public void setX(int x) {
        if (x > 9 || x < 0) {
            throw new IllegalArgumentException("Error! Coordinate X is out of range!");
        }
        this.x = x;
    }
    public void setY(int y) {
        if (y > 9 || y < 0) {
            throw new IllegalArgumentException("Error! Coordinates Y is out of range!");
        }
        this.y = y;
    }
    public int getY() {
        return y;
    }

    public static boolean isValidCoordinates(Coordinates coord) {
        return coord.getX() > 9 || coord.getX() < 0 || coord.getY() > 9 || coord.getY() < 0;
    }
}
class BattleField {

    private boolean fogOfWarOn;
    private ArrayList<AbstractBattleship> fleet;
    private char[][] map;

    public BattleField() {
        map = new char[10][10];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = '~';
            }
        }
        fleet = new ArrayList<AbstractBattleship>();
    }

    public boolean isFogOfWarOn() {
        return fogOfWarOn;
    }

    public void setFogOfWarOn(boolean isTurnedOn) {
        fogOfWarOn = isTurnedOn;
    }
    public boolean isHit(Coordinates coords) {
        if (map[coords.getY()][coords.getX()] == 'O' || map[coords.getY()][coords.getX()] == 'X') {
            map[coords.getY()][coords.getX()] = 'X';
            return true;
        } else {
            map[coords.getY()][coords.getX()] = 'M';
            return false;
        }
    }
    private void addPadding(AbstractBattleship battleship) {
        Coordinates[] coords = battleship.getCoordinates();
        boolean isHorizontal = true;
        isHorizontal = coords[0].getY() == coords[coords.length - 1].getY();
        if (isHorizontal) {
            for (int j = coords[0].getX() - 1; j <= coords[0].getX() + coords.length; j++) {
                try {
                    map[coords[0].getY() - 1][j] = '·';
                } catch (ArrayIndexOutOfBoundsException e) {}
            }

            try {
                map[coords[0].getY()][coords[0].getX() - 1] = '·';
            } catch (ArrayIndexOutOfBoundsException e) {}

            try {
                map[coords[0].getY()][coords[coords.length - 1].getX() + 1] = '·';
            } catch (ArrayIndexOutOfBoundsException e) {}

            for (int j = coords[0].getX() - 1; j <= coords[0].getX() + coords.length; j++) {
                try {
                    map[coords[0].getY() + 1][j] = '·';
                } catch (ArrayIndexOutOfBoundsException e) {}
            }
        } else {
            for (int j = coords[0].getY() - 1; j <= coords[0].getY() + coords.length; j++) {
                try {
                    map[j][coords[0].getX() - 1] = '·';
                } catch (ArrayIndexOutOfBoundsException e) {}
            }

            try {
                map[coords[0].getY() - 1][coords[0].getX()] = '·';
            } catch (ArrayIndexOutOfBoundsException e) {}

            try {
                map[coords[coords.length - 1].getY() + 1][coords[0].getX()] = '·';
            } catch (ArrayIndexOutOfBoundsException e) {}

            for (int j = coords[0].getY() - 1; j <= coords[0].getY() + coords.length; j++) {
                try {
                    map[j][coords[0].getX() + 1] = '·';
                } catch (ArrayIndexOutOfBoundsException e) {}
            }
        }
    }

    public void addBattleShip(AbstractBattleship battleship) {
        Coordinates[] coords = battleship.getCoordinates();
        Coordinates[] buffer = new Coordinates[coords.length];
        for (int i = 0; i < coords.length; i++) {
            if (map[coords[i].getY()][coords[i].getX()] == '·') {
                throw new IllegalArgumentException("Error! Wrong coordinates! Too close to other ship.");
            } else if (map[coords[i].getY()][coords[i].getX()] == 'O') {
                throw new IllegalArgumentException("Error! Wrong coordinates! Overlapping with other ship.");
            }
            buffer[i] = new Coordinates(coords[i].getX(), coords[i].getY());
        }
        for (int i = 0; i < buffer.length; i++) {
            map[buffer[i].getY()][buffer[i].getX()] = 'O';
        }
        fleet.add(battleship);
        addPadding(battleship);
    }

    public void drawBattleField() {
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', };
        System.out.println("\s\s1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < map.length; i++) {
            System.out.print(letters[i]);
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == '·' ||
                    fogOfWarOn && map[i][j] == 'O') {
                    System.out.print(" ~");
                } else if (map[i][j] == 'X') {
                    System.out.print(" X");
                } else if (map[i][j] == 'M') {
                    System.out.print(" M");
                } else {
                    System.out.print(" " + map[i][j]);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public AbstractBattleship getBattleShipByCoords(Coordinates coords) {
        for (int i = 0; i < fleet.size(); i++) {
            for (int j = 0; j < fleet.get(i).getCoordinates().length; j++) {
                if (fleet.get(i).getCoordinates()[j].getX() == coords.getX() &&
                        fleet.get(i).getCoordinates()[j].getY() == coords.getY()) {
                    return fleet.get(i);
                }
            }
        }
        return null;
    }

    public boolean isAnyoneAfloat() {
        for (int i = 0; i < fleet.size(); i++) {
            if (fleet.get(i).isAfloat(this)) {
                return true;
            }
        }
        return false;
    }

    public char getMapSymbol(Coordinates coords) {
        return map[coords.getY()][coords.getX()];
    }
}
abstract class AbstractBattleship {

    protected Coordinates[] coordinates;

    protected int length;

    protected AbstractBattleship(int length) {
        this.length = length;
        coordinates = new Coordinates[length];
    }
    public Coordinates[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates begining, Coordinates end) {
        if (begining.getX() == end.getX()) {
            if (Math.abs(end.getY() - begining.getY() + 1) != this.length) {
                throw new IllegalArgumentException(("Error! Wrong coordinates, too long or two short!"));
            }
            if (begining.getY() + this.length - 1> 9) {
                throw new IllegalArgumentException(("Error! The battleship cannot be placed here."));
            }

            for (int i = 0; i < length; i++) {
                coordinates[i] = new Coordinates(begining.getX(), begining.getY() + i);
            }
        } else if (begining.getY() == end.getY()) {
            if (Math.abs(end.getX() - begining.getX() + 1) != this.length) {
                throw new IllegalArgumentException(("Error! Wrong coordinates, too long or two short!"));
            }
            if (begining.getX() + this.length - 1 > 9) {
                throw new IllegalArgumentException(("Error! The battleship cannot be placed here."));
            }

            for (int i = 0; i < length; i++) {
                coordinates[i] = new Coordinates(begining.getX() + i, begining.getY());
            }
        } else {
            throw new IllegalArgumentException("Error! A battleship cannot by placed by diagonal.");
        }
    }

    public String getBattleshipType() {
        switch (length) {
            case 5:
                return "Aircraft Carrier";
            case 4:
                return "Battleship";
            case 3:
                return "Submarine";
            default:
                return "Destroyer";
        }
    }

    public boolean isAfloat(BattleField battleField) {
        for (int i = 0; i < coordinates.length; i++) {
            if (battleField.getMapSymbol(coordinates[i]) == 'O') {
                return true;
            }
        }
        return false;
    }
}

class AircraftCarrier extends AbstractBattleship {
    public AircraftCarrier() {
        super(5);
    }
}

class Battleship extends AbstractBattleship {
    public Battleship() {
        super(4);
    }
}

class Submarine extends AbstractBattleship {
    public Submarine() {
        super(3);
    }
}

class Cruiser extends AbstractBattleship {
    public Cruiser() {
        super(3);
    }

    @Override
    public String getBattleshipType() {
        return "Cruiser";
    }
}

class Destroyer extends AbstractBattleship {
    public Destroyer() {
        super(2);
    }
}

class Player {

    String playerName;
    AbstractBattleship[] fleet;
    BattleField battleField;
    public Player(BattleField battleField, String playerName) {
        this.playerName = playerName;
        this.battleField = battleField;
        fleet = new AbstractBattleship[5];
        fleet[0] = new AircraftCarrier();
        fleet[1] = new Battleship();
        fleet[2] = new Submarine();
        fleet[3] = new Cruiser();
        fleet[4] = new Destroyer();
    }

    public String getPlayerName() {
        return playerName;
    }
    public AbstractBattleship[] getFleet() {
        return fleet;
    }
    public BattleField getBattleField() {
        return battleField;
    }
}
