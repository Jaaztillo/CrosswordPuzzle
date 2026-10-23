public class Word {
    private String word, description;

    private int direction; // 0 -> horizontal | 1 -> vertical
    private int size;
    private int index;

    private int[] position;

    public Word(String word, String description) {
        this.word = word;
        this.description = description;

        this.size = word.length();
    }

    public String getWord() { return word; }

    public String getDescription() { return description; }

    public int getDirection() { return direction; }

    public int getSize() { return size; }

    public int getIndex() { return index; }

    public int[] getPosition() { return position; }

    public void setPosition(int[] position) { this.position = position; }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setIndex(int index) { this.index = index+1; }
}