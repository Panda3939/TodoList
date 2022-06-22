public enum Category {
    RED("\033[0;31m"),
    GREEN("\033[0;32m"),
    BLUE("\033[0;34m"),
    PURPLE("\033[0;35m"),
    YELLOW("\033[0;33m"),
    WHITE("\033[0;37m");

    private String colour;

    Category(String c){
        colour = c;
    }

    public String getColour(){
        return colour;
    }

}
