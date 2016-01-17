public enum HandFigureTypes {
    SCISSORS {
        public String toString() {
            return "scissors";
        }
    },
    NET {
        public String toString() {
            return "net";
        }
    },
    WELL {
        public String toString() {
            return "well";
        }
    };



    public static Player getWinner(HandFigureTypes playerFigure, HandFigureTypes computerFigure){
        if(playerFigure.equals(computerFigure)) return Player.NONE;
        switch (playerFigure){
            case SCISSORS:
                if(computerFigure.equals(HandFigureTypes.NET))
                    return Player.HUMAN;
                return Player.COMPUTER;
            case NET:
                if(computerFigure.equals(HandFigureTypes.WELL))
                    return Player.HUMAN;
                return Player.COMPUTER;
            default:
                if(computerFigure.equals(HandFigureTypes.SCISSORS))
                    return Player.HUMAN;
                return Player.COMPUTER;
        }
    }
}
