package pnj.ac.id.rifqi_afif.db;

public class PicturesTable
{
    private int id;
    private String username;
    private String name;
    private byte[] picture;

    public int getId(){return id;}
    public void setId(int id){this.id = id;}

    public String getUsername(){return username;}
    public void setUsername(String username){this.username = username;}

    public String getName(){return name;}
    public void setName(String name){this.name = name;}

    public byte[] getPicture(){return picture;}
    public void setPicture(byte[] picture){this.picture = picture;}
}
