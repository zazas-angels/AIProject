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



    public static int getWinner(HandFigureTypes playerFigure, HandFigureTypes computerFigure){
        if(playerFigure.equals(computerFigure)) return 0;
        switch (playerFigure){
            case SCISSORS:
                if(computerFigure.equals(HandFigureTypes.NET))
                    return 1;
                return -1;
            case NET:
                if(computerFigure.equals(HandFigureTypes.WELL))
                    return 1;
                return -1;
            default:
                if(computerFigure.equals(HandFigureTypes.SCISSORS))
                    return 1;
                return -1;
        }
    }
}
