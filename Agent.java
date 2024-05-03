///SadMith,adam02.kovacs@gmail.com
import java.util.*;
import game.racetrack.Direction;
import game.racetrack.RaceTrackGame;
import game.racetrack.RaceTrackPlayer;
import game.racetrack.utils.Coin;
import game.racetrack.utils.PathCell;
import game.racetrack.utils.PlayerState;

/**
 * Az "Agent" osztály a RaceTrack játék egy játékosát reprezentálja.
 * Ez az osztály az AI-alapú döntéshozatalért felelős, ami meghatározza a játékos következő lépését a versenypályán.
 * Az ideális út kiszámításában a BFS algortimus segít
 */
public class Agent extends RaceTrackPlayer {

    /**
     * Konstruktor az "Agent" osztályhoz.
     *
     * @param state A játékos jelenlegi állapota.
     * @param random Egy Random példány, ami véletlenszám-generálásra használható.
     * @param track A versenypálya reprezentációja.
     * @param coins A pályán elhelyezkedő érmék tömbje.
     * @param color A játékos színe.
     */
    public Agent(PlayerState state, Random random, int[][] track, Coin[] coins, int color) {
        super(state, random, track, coins, color);
    }
/** A path lista az optimális útvanalat fogja tartalmazni */
    private List<PathCell> path = new LinkedList<>();

    /**
     * Meghatározza a játékos következő irányát a megmaradt idő függvényében.
     *
     * @param remainingTime A kör végéig hátralévő idő.
     * @return A kiválasztott irány.
     */
    @Override
    public Direction getDirection(long remainingTime) {



        int currentI = state.i;
        int currentJ = state.j;


        if (path.isEmpty()) {
            path = RaceTrackGame.BFS(currentI, currentJ, track);
            path.remove(0);
        }




        if ((!path.isEmpty())){

        }

        if (!path.isEmpty()) {
            PathCell nextStep = path.get(0);
            PathCell iranyStep = nextStep;
            int newI = nextStep.i - currentI;
            int newJ = nextStep.j - currentJ;

            PlayerState currentState= state;
            int hosszusag=0;
            PlayerState next=null;
            Direction iranySebbeseg = direkcio();


            for (PathCell NewpathElem: path) {

                next = new PlayerState(NewpathElem.i, NewpathElem.j, 0, 0);


                Direction tovabbiSebb = tavolsag(currentState,next);

                if (azonosIrany(iranySebbeseg, tovabbiSebb))hosszusag++;
                else break;

                currentState = next;


            }

            PlayerState jelenlegiState = state;

            int c = calculateMaxDistance(currentState,jelenlegiState);

            int v = calculateMaxVelocity(jelenlegiState);

            if (hosszusag ==0) {

                if((iranyStep.i - jelenlegiState.i)>1 || 1<(iranyStep.j - jelenlegiState.j)) {
                    return iranyValamerre((iranyStep.i - jelenlegiState.i)/2, (iranyStep.j - jelenlegiState.j)/2);
                }

                path.remove(0);
                return iranyValamerre(iranyStep.i - jelenlegiState.i, iranyStep.j - jelenlegiState.j);
            }
            if (isSumGreaterOrEqual(c, v + 1)) {
                for (int i=0;i<v+1;i++) {
                    path.remove(0);
                }


                for (Direction dir : RaceTrackGame.DIRECTIONS) {
                    if(dir.i == iranySebbeseg.i && dir.j == iranySebbeseg.j ){
                        return dir;}
                }
            }
            else if (isSumGreaterOrEqual(c, v))
            { // tartas
                for (int i=0;i<v;i++) {

                    path.remove(0);
                }
                return RaceTrackGame.DIRECTIONS[0];
            }
            else
            { //Brembo fek mester!
                for (int i=0;i<v-1;i++) {


                    path.remove(0);
                }
                if (direkcio().i == 0 && direkcio().j == -1) {
                    return RaceTrackGame.DIRECTIONS[5];
                } else if (direkcio().i== -1 && direkcio().j == -1) {
                    return RaceTrackGame.DIRECTIONS[6];

                } else if (direkcio().i == -1 && direkcio().j == 0) {
                    return RaceTrackGame.DIRECTIONS[7];
                } else if (direkcio().i == -1 && direkcio().j == 1) {
                    return RaceTrackGame.DIRECTIONS[8];
                } else if (direkcio().i == 0 && direkcio().j == 1) {
                    return RaceTrackGame.DIRECTIONS[1];
                } else if (direkcio().i == 1 && direkcio().j == 1 ) {
                    return new Direction(-1,-1);
                } else if (direkcio().i == 1 && direkcio().j == 0) {
                    return RaceTrackGame.DIRECTIONS[3];
                } else if (direkcio().i == 1 && direkcio().j == -1) {
                    return RaceTrackGame.DIRECTIONS[4];
                }
            }





            iranyValamerre(newI,newJ);


            Direction newDirection = new Direction(newI, newJ);

            return newDirection;
        } else {


            path = RaceTrackGame.BFS(state.i, state.j, track);
            PathCell nextStep = path.get(1);
            int newI = nextStep.i - currentI;
            int newJ = nextStep.j - currentJ;

            iranyValamerre(newI,newJ);

            Direction newDirection = new Direction(currentI, currentJ);

            return newDirection;
        }

    }


    /**
     * Meghatározza, hogy egy adott távolság nagyobb vagy egyenlő-e egy adott korláttal.
     *
     * @param threshold A korlát értéke.
     * @param limit Az összehasonlítandó távolság.
     * @return Igaz, ha a távolság nagyobb vagy egyenlő a korláttal.
     */
    public boolean isSumGreaterOrEqual(int threshold, int limit) {
        int sum = (limit * (limit + 1)) / 2;
        return threshold >= sum;
    }


    /**
     * Meghatározza az aktuális irányt az állapot alapján.
     *
     * @return Az aktuális irány, amit a játékos sebességének előjele határoz meg.
     */
    private Direction direkcio() {
        int xDirection = Integer.compare(state.vi, 0);
        int yDirection = Integer.compare(state.vj, 0);
        return new Direction(xDirection, yDirection);
    }

    /**
     * Kiszámítja a távolságot két állapot között.
     *
     * @param EztVonjukKi Az állapot, amiből kiindulunk.
     * @param Ebbol Az állapot, amely felé haladunk.
     * @return A két állapot közötti távolság iránya.
     */
    private Direction tavolsag(PlayerState EztVonjukKi, PlayerState Ebbol)
    {
        return new Direction(Ebbol.i - EztVonjukKi.i,  Ebbol.j - EztVonjukKi.j);
    }


    /**
     * Meghatározza az irányt a megadott koordináták alapján.
     *
     * @param newI Az i koordináta iránya.
     * @param newJ Az j koordináta iránya.
     * @return Az új irány, vagy null, ha nincs ilyen irány.
     */
    private Direction iranyValamerre(int newI, int newJ) {
        for (Direction dir : RaceTrackGame.DIRECTIONS) {
            if (dir.i == newI && dir.j == newJ) {
                if (masikIrany(dir)) {
                    return new Direction(-state.vi, -state.vj);
                }
                return new Direction(dir.i - state.vi, dir.j - state.vj);
            }
        }
        return null;
    }

    /**
     * Ellenőrzi, hogy a megadott irány ellentétes-e az aktuális sebességi iránnyal.
     *
     * @param dir Az ellenőrizendő irány.
     * @return Igaz, ha az irány ellentétes az aktuális sebességgel.
     */
    private boolean masikIrany(Direction dir) {
        return (state.vi == 1 && dir.i == -1) || (state.vi == -1 && dir.i == 1) ||
                (state.vj == 1 && dir.j == -1) || (state.vj == -1 && dir.j == 1);
    }

    /**
     * Kiszámítja a maximális távolságot két állapot között.
     *
     * @param currentState Az aktuális állapot.
     * @param jelenlegiState A célállapot.
     * @return A maximális távolság az i és j koordináták között.
     */
    private int calculateMaxDistance(PlayerState currentState, PlayerState jelenlegiState) {
        int deltaI = calculateAbsoluteDifference(currentState.i, jelenlegiState.i);
        int deltaJ = calculateAbsoluteDifference(currentState.j, jelenlegiState.j);
        return Math.max(deltaI, deltaJ);
    }

    /**
     * Kiszámítja a maximális sebesség írányt egy adott állapotban.
     *
     * @param state Az állapot, amiben a sebességet mérjük.
     * @return A maximális sebesség abszolút értéke.
     */
    private int calculateMaxVelocity(PlayerState state) {
        return Math.max(Math.abs(state.vi), Math.abs(state.vj));
    }

    /**
     * Kiszámítja a két szám abszolút értékének különbségét.
     *
     * @param a Az első szám. Ami egy koordináta.
     * @param b A második szám.Ami egy koordináta.
     * @return Az abszolút különbség a két szám között.
     */
    private int calculateAbsoluteDifference(int a, int b) {
        return Math.abs(a - b);
    }

    /**
     * Ellenőrzi, hogy két irány azonos-e.
     *
     * @param dir1 Az első irány.
     * @param dir2 A második irány.
     * @return Igaz, ha a két irány megegyezik.
     */
    private boolean azonosIrany(Direction dir1, Direction dir2) {
        return dir1.i == dir2.i && dir1.j == dir2.j;
    }




}