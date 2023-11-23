package software.tnb.product.integration;

public class Resource {
    private String name;
    private String content;
    private boolean isContentPath;

    protected Resource() {
    }

    public Resource(String name, String content) {
        this(name, content, false);
    }

    public Resource(String name, String content, boolean isContentPath) {
        this.name = name;
        this.content = content;
        this.isContentPath = isContentPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean getIsContentPath() {
        return isContentPath;
    }
}
