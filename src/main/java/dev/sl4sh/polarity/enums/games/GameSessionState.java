package dev.sl4sh.polarity.enums.games;

public enum GameSessionState {

    INACTIVE,
    WAITING_FOR_PLAYERS,
    LAUNCHING,
    PRE_GAME,
    RUNNING,
    FINISHING,
    OVER;

    public String getNiceName(){

        switch (this){

            case INACTIVE:
                return "Inactive";
            case WAITING_FOR_PLAYERS:
                return "Waiting for players";
            case LAUNCHING:
                return "Launching";
            case RUNNING:
                return "Running";
            case PRE_GAME:
                return "Pre game";
            case OVER:
                return "Over";
            case FINISHING:
                return "Finishing";

        }

        return "Unknown";

    }

}
