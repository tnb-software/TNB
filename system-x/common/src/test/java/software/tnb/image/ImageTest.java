package software.tnb.image;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import software.tnb.common.deployment.WithDockerImage;
import software.tnb.image.util.ChildClass;
import software.tnb.image.util.ParentClass;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
public class ImageTest {
    @Test
    public void shouldThrowExceptionWhenThereIsNoSuperclassTest() {
        ParentClass tc = new ParentClass();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(tc::image);
    }

    @Test
    public void shouldReturnDefaultImageFromSuperclassTest() {
        ChildClass cc = new ChildClass();
        assertThat(cc.image()).isEqualTo("parentclassdefaultimage");
    }

    @Test
    public void shouldOverrideImageWithSuperclassNameTest() {
        ChildClass cc = new ChildClass();
        final String image = "overrideimage";
        try {
            System.setProperty(String.format(WithDockerImage.SYSTEM_PROPERTY_FORMAT, "parentclass"), image);
            assertThat(cc.image()).isEqualTo(image);
        } finally {
            System.clearProperty(String.format(WithDockerImage.SYSTEM_PROPERTY_FORMAT, "parentclass"));
        }
    }
}
