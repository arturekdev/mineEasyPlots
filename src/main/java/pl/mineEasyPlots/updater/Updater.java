package pl.mineEasyPlots.updater;

import org.bukkit.plugin.Plugin;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import static java.lang.Integer.*;

public class Updater {
    public static final String VERSION = "SPU 2.0 by stealth-coders";
    private final Plugin plugin;
    private final String pluginUrl;
    private URL url;
    private boolean isCanceled = false;

    private String version;

    private String downloadURL;

    private String changeLog;

    private boolean out;

    public Updater(Plugin plugin, String pluginUrl) {
        this.version = "";
        this.out = true;
        try {
            this.url = new URL(pluginUrl);
        } catch (MalformedURLException e) {
            this.isCanceled = true;
            plugin.getLogger().log(Level.WARNING, "Error: Bad URL while checking {0} !", plugin.getDescription().getName());
        }
        this.plugin = plugin;
        this.pluginUrl = pluginUrl;
    }

    public void enableOut() {
        this.out = true;
    }

    public void disableOut() {
        this.out = false;
    }

    public boolean needsUpdate() {
        if (this.isCanceled)
            return false;
        try {
            URLConnection con = this.url.openConnection();
            InputStream _in = con.getInputStream();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(_in);
            Node nod = doc.getElementsByTagName("item").item(0);
            NodeList children = nod.getChildNodes();
            this.version = children.item(1).getTextContent();
            if (newVersionAvailable(this.plugin.getDescription().getVersion(), this.version.replaceAll("[a-zA-z ]", ""))) {
                return true;
            }
        } catch (IOException | org.xml.sax.SAXException | javax.xml.parsers.ParserConfigurationException e) {
            System.out.println(" Error for check new version: " + this.plugin.getDescription().getName());
            System.out.println(" Error: " + e);
        }
        return false;
    }

    public String getNewVersion() {
        if (this.isCanceled)
            return "";
        try {
            URLConnection con = this.url.openConnection();
            InputStream _in = con.getInputStream();
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(_in);
            Node nod = doc.getElementsByTagName("item").item(0);
            NodeList children = nod.getChildNodes();
            this.version = children.item(1).getTextContent();

            return this.version.replaceAll("[a-zA-z ]", "");
        } catch (IOException | org.xml.sax.SAXException | javax.xml.parsers.ParserConfigurationException e) {
            System.out.println(" Error for check new version: " + this.plugin.getDescription().getName());
            System.out.println(" Error: " + e);
        }
        return "";
    }

    public boolean newVersionAvailable(String oldv, String newv) {
        if (oldv != null && newv != null) {
            oldv = oldv.replace('.', '_');
            newv = newv.replace('.', '_');
            if ((oldv.split("_")).length != 0 && (oldv.split("_")).length != 1 && (newv.split("_")).length != 0 && (newv.split("_")).length != 1) {
                int vnum = parseInt(oldv.split("_")[0]);
                int vsec = parseInt(oldv.split("_")[1]);
                int newvnum = parseInt(newv.split("_")[0]);
                int newvsec = parseInt(newv.split("_")[1]);
                if (newvnum > vnum)
                    return true;
                return newvnum == vnum &&
                        newvsec > vsec;
            }
        }
        return false;
    }

}