/**
 * DO NOT MODIFY THIS CODE
 * There are no user serviceable components.  Any changes will void your warranty.
 * @author Someone
 * @author Grant Yang
 * @version 2022.03.15
 */
public interface ArrowListener
{
    /**
     * Callback function for when the A key is pressed
     * (was originally the up key, was rebound to A)
     */
    void upPressed();

    /**
     * Callback function for when the down arrow is pressed
     */
    void downPressed();

    /**
     * Callback function for when the left arrow is pressed
     */
    void leftPressed();

    /**
     * Callback function for when the right arrow is pressed
     */
    void rightPressed();

    /**
     * Callback function for when the r key is pressed (unused)
     */
    void rPressed();

    /**
     * Callback function for when the space key is pressed for insta-drop
     */
    void instaDrop();
}