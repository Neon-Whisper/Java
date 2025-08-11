import java.util.Random;

public class Titan {

    int HP;

    public int getHP() {
        return HP;
    }

    public void setHP(int HP) {
        this.HP = HP;
    }

    public Titan() {
        this.HP = 700;
    }

    public Titan(int HP) {
        this.HP = HP;
    }

    public void attack(Zues z) {
        Random r = new Random();
        int hit = r.nextInt(91) + 10;
        z.setHP(z.getHP() - hit);
        System.out.println("泰坦攻击宙斯，产生 " + hit + "点伤害，宙斯当前HP为" + z.getHP());
    }
}
