import java.util.Random;

public class Zues {

    int HP;

    public Zues()
    {
        HP = 1000;
    }

    public Zues(int HP)
    {
        this.HP = HP;
    }

    public int getHP()
    {
        return HP;
    }

    public void setHP(int HP)
    {
        this.HP = HP;
    }

    public void attack(Titan t)
    {
        Random r = new Random();
        int hit = r.nextInt(71);
        t.setHP(t.getHP() - hit);
        System.out.println("宙斯攻击泰坦，产生" + hit + "点伤害，泰坦当前HP为" + t.getHP());

    }
}
