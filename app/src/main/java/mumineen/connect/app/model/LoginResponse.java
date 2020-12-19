package mumineen.connect.app.model;

public class LoginResponse
{

     String member_id_pk;
     String ejamaat_id;
     String sabil_no;
     String member_hof_id;
     String web_url;
     String handshake;
     String app_register_url;

    public String getMember_id_pk() {
        return member_id_pk;
    }

    public void setMember_id_pk(String member_id_pk) {
        this.member_id_pk = member_id_pk;
    }

    public String getEjamaat_id() {
        return ejamaat_id;
    }

    public void setEjamaat_id(String ejamaat_id) {
        this.ejamaat_id = ejamaat_id;
    }

    public String getSabil_no() {
        return sabil_no;
    }

    public void setSabil_no(String sabil_no) {
        this.sabil_no = sabil_no;
    }

    public String getMember_hof_id() {
        return member_hof_id;
    }

    public void setMember_hof_id(String member_hof_id) {
        this.member_hof_id = member_hof_id;
    }

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public String getHandshake() {
        return handshake;
    }

    public void setHandshake(String handshake) {
        this.handshake = handshake;
    }

    public String getApp_register_url() {
        return app_register_url;
    }

    public void setApp_register_url(String app_register_url) {
        this.app_register_url = app_register_url;
    }
}
