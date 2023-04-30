package battleship;

import java.lang.Math;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        BattleField battleField = new BattleField();
        AbstractBattleship[] fleet = new AbstractBattleship[5];
        fleet[0] = new AircraftCarrier();
        fleet[1] = new Battleship();
        fleet[2] = new Submarine();
        fleet[3] = new Cruiser();
        fleet[4] = new Destroyer();
        placeFleet(battleField, fleet);

    }

    private static void placeFleet(BattleField battleField, AbstractBattleship[] fleet) {
        Scanner scanner = new Scanner(System.in);
        Parser parser = new Parser();
        String input;
        for (int i = 0; i < fleet.length; i++) {
            battleField.drawBattleField();
            System.out.printf("Enter the coordinates of the %s (%d cells):\r\n", fleet[i].getBattleshipType(), fleet[i].length);
            boolean stop = false;
            while (!stop) {
                try {
                    input = scanner.nextLine();
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
//                    battleField.drawBattleField();
                    stop = true;
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage() + " Try again:");
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
    private char[][] map;

    public BattleField() {
        map = new char[10][10];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = '~';
            }
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
        for (int i = 0; i < coords.length; i++) {
            if (map[coords[i].getY()][coords[i].getX()] == '·') {
                throw new IllegalArgumentException("Error! Wrong coordinates! Too close to other ship.");
            } else if (map[coords[i].getY()][coords[i].getX()] == 'O') {
                throw new IllegalArgumentException("Error! Wrong coordinates! Overlapping with other ship.");
            }
            map[coords[i].getY()][coords[i].getX()] = 'O';
        }
        addPadding(battleship);
    }

    public void drawBattleField() {
        char[] letters = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', };
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < map.length; i++) {
            System.out.print(letters[i]);
            for (int j = 0; j < map[i].length; j++) {
                if (map[i][j] == '·') {
                    System.out.print(" ~");
                } else {
                    System.out.print(" " + map[i][j]);
                }
            }
            System.out.print("\r\n");
        }
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
