package MCTS_Framework;

public class Player {
    private String m_name;
    private int m_id;
    public Player(String name, int id) {
        m_name = name;
        m_id = id;
    }

    public String getName() {
        return m_name;
    }

    public int getId() {
        return m_id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player))
            return false;
        Player otherPlayer = (Player) obj;
        return m_id == otherPlayer.m_id;
    }
}