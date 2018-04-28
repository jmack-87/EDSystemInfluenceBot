package com.jmack.EDSystemInfluenceBot;

import java.io.IOException;
//import java.util.List;
//import java.util.Random;

import javax.security.auth.login.LoginException;

import com.jmack.EDSystemInfluenceBot.SystemFactionInfluence;

import net.dv8tion.jda.client.entities.Group;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
//import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.PrivateChannel;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
//import net.dv8tion.jda.core.exceptions.PermissionException;
//import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class TriggerListener extends ListenerAdapter {
	
	private static String token = "NDM2NjQ1MjI2NDIyMDc1NDAw.DcZ5bA.F0gRoR6uT5YKrG-jbneJOUnC5-s"; //don't push to GitHub with actual token!!!

    /**
     * This is the method where the program starts.
     */
    @SuppressWarnings("unused")
	public static void main(String[] args)
    {
        //We construct a builder for a BOT account. If we wanted to use a CLIENT account
        // we would use AccountType.CLIENT
        try
        {
            JDA jda = new JDABuilder(AccountType.BOT)
                    .setToken(token)           //The token of the account that is logging in.
                    .addEventListener(new TriggerListener())  //An instance of a class that will handle events.
                    .buildBlocking();  //There are 2 ways to login, blocking vs async. Blocking guarantees that JDA will be completely loaded.
        }
        catch (LoginException e)
        {
            //If anything goes wrong in terms of authentication, this is the exception that will represent it
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            //Due to the fact that buildBlocking is a blocking method, one which waits until JDA is fully loaded,
            // the waiting can be interrupted. This is the exception that would fire in that situation.
            //As a note: in this extremely simplified example this will never occur. In fact, this will never occur unless
            // you use buildBlocking in a thread that has the possibility of being interrupted (async thread usage and interrupts)
            e.printStackTrace();
        }
    }

    /**
     * NOTE THE @Override!
     * This method is actually overriding a method in the ListenerAdapter class! We place an @Override annotation
     *  right before any method that is overriding another to guarantee to ourselves that it is actually overriding
     *  a method from a super class properly. You should do this every time you override a method!
     *
     * As stated above, this method is overriding a hook method in the
     * {@link net.dv8tion.jda.core.hooks.ListenerAdapter ListenerAdapter} class. It has convience methods for all JDA events!
     * Consider looking through the events it offers if you plan to use the ListenerAdapter.
     *
     * In this example, when a message is received it is printed to the console.
     *
     * @param event
     *          An event containing information about a {@link net.dv8tion.jda.core.entities.Message Message} that was
     *          sent in a channel.
     */
    @SuppressWarnings("unused")
	@Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        //These are provided with every event in JDA
        JDA jda = event.getJDA();                       //JDA, the core of the api.
        long responseNumber = event.getResponseNumber();//The amount of discord events that JDA has received since the last reconnect.

        //Event specific information
        User author = event.getAuthor();                //The user that sent the message
        Message message = event.getMessage();           //The message that was received.
        MessageChannel channel = event.getChannel();    //This is the MessageChannel that the message was sent to.
                                                        //  This could be a TextChannel, PrivateChannel, or Group!

        String msg = message.getContentDisplay();       //This returns a human readable version of the Message. Similar to
                                                        // what you would see in the client.

        boolean bot = author.isBot();                    //This boolean is useful to determine if the User that
                                                        // sent the Message is a BOT or not!

        if (event.isFromType(ChannelType.TEXT))         //If this message was sent to a Guild TextChannel
        {
            //Because we now know that this message was sent in a Guild, we can do guild specific things
            // Note, if you don't check the ChannelType before using these methods, they might return null due
            // the message possibly not being from a Guild!

            Guild guild = event.getGuild();             //The Guild that this message was sent in. (note, in the API, Guilds are Servers)
            TextChannel textChannel = event.getTextChannel(); //The TextChannel that this message was sent to.
            Member member = event.getMember();          //This Member that sent the message. Contains Guild specific information about the User!

            String name;
            if (message.isWebhookMessage())
            {
                name = author.getName();                //If this is a Webhook message, then there is no Member associated
            }                                           // with the User, thus we default to the author for name.
            else
            {
                name = member.getEffectiveName();       //This will either use the Member's nickname if they have one,
            }                                           // otherwise it will default to their username. (User#getName())
            	
//            if (!name.equals("EDSystemInfluence")) {
//            	System.out.printf("(%s)[%s]<%s>: %s\n", guild.getName(), textChannel.getName(), name, msg);
//            }
        }
        else if (event.isFromType(ChannelType.PRIVATE)) //If this message was sent to a PrivateChannel
        {
            //The message was sent in a PrivateChannel.
            //In this example we don't directly use the privateChannel, however, be sure, there are uses for it!
            PrivateChannel privateChannel = event.getPrivateChannel();

            System.out.printf("[PRIV]<%s>: %s\n", author.getName(), msg);
        }
        else if (event.isFromType(ChannelType.GROUP))   //If this message was sent to a Group. This is CLIENT only!
        {
            //The message was sent in a Group. It should be noted that Groups are CLIENT only.
            Group group = event.getGroup();
            String groupName = group.getName() != null ? group.getName() : "";  //A group name can be null due to it being unnamed.

            System.out.printf("[GRP: %s]<%s>: %s\n", groupName, author.getName(), msg);
        }


        //Now that you have a grasp on the things that you might see in an event, specifically MessageReceivedEvent,
        // we will look at sending / responding to messages!
        //This will be an extremely simplified example of command processing.

        //Remember, in all of these .equals checks it is actually comparing
        // message.getContentDisplay().equals, which is comparing a string to a string.
        // If you did message.equals() it will fail because you would be comparing a Message to a String!
//        if (msg.equals("!ping"))
//        {
            //This will send a message, "pong!", by constructing a RestAction and "queueing" the action with the Requester.
            // By calling queue(), we send the Request to the Requester which will send it to discord. Using queue() or any
            // of its different forms will handle ratelimiting for you automatically!

//            channel.sendMessage("pong!").queue();
//        }
//        else if (msg.equals("!roll"))
//        {
            //In this case, we have an example showing how to use the Success consumer for a RestAction. The Success consumer
            // will provide you with the object that results after you execute your RestAction. As a note, not all RestActions
            // have object returns and will instead have Void returns. You can still use the success consumer to determine when
            // the action has been completed!

//            Random rand = new Random();
//            int roll = rand.nextInt(6) + 1; //This results in 1 - 6 (instead of 0 - 5)
//            channel.sendMessage("Your roll: " + roll).queue(sentMessage ->  //This is called a lambda statement. If you don't know
//            {                                                               // what they are or how they work, try google!
//                if (roll < 3)
//                {
//                    channel.sendMessage("The roll for messageId: " + sentMessage.getId() + " wasn't very good... Must be bad luck!\n").queue();
//                }
//            });
//        }
        

//        else if (msg.toLowerCase().startsWith("mate.influence ")) {
        	if (msg.toLowerCase().startsWith("mate.influence ")) {
        	//if (msg.contains("<") && msg.contains(">")) {
        		//String system = msg.substring(msg.indexOf("<")+1, msg.indexOf(">"));
        	if (msg.length() > 15) {
        		String system = msg.substring(msg.indexOf(" ")+1).trim();
        		
        		try {
    				channel.sendMessage(SystemFactionInfluence.main(system).build()).queue();
    				
    			} catch (IOException e) {
    				e.printStackTrace();
    			};
    			
        	} else {
        		channel.sendMessage(author.getAsMention() + " ```Incorrect format. Use: '!influence:<system name>'```").queue();
        	}
        	SystemFactionInfluence.purgeEmbed();
        }
        
//        else if (msg.startsWith("!kick"))   //Note, I used "startsWith, not equals.
//        {
            //This is an admin command. That means that it requires specific permissions to use it, in this case
            // it needs Permission.KICK_MEMBERS. We will have a check before we attempt to kick members to see
            // if the logged in account actually has the permission, but considering something could change after our
            // check we should also take into account the possibility that we don't have permission anymore, thus Discord
            // response with a permission failure!
            //We will use the error consumer, the second parameter in queue!

            //We only want to deal with message sent in a Guild.
//            if (message.isFromType(ChannelType.TEXT))
//            {
                //If no users are provided, we can't kick anyone!
//                if (message.getMentionedUsers().isEmpty())
//                {
//                    channel.sendMessage("You must mention 1 or more Users to be kicked!").queue();
//                }
//                else
//                {
//                    Guild guild = event.getGuild();
//                    Member selfMember = guild.getSelfMember();  //This is the currently logged in account's Member object.
//                                                                // Very similar to JDA#getSelfUser()!

                    //Now, we the the logged in account doesn't have permission to kick members.. well.. we can't kick!
//                    if (!selfMember.hasPermission(Permission.KICK_MEMBERS))
//                    {
//                        channel.sendMessage("Sorry! I don't have permission to kick members in this Guild!").queue();
//                        return; //We jump out of the method instead of using cascading if/else
//                    }

                    //Loop over all mentioned users, kicking them one at a time. Mwauahahah!
//                    List<User> mentionedUsers = message.getMentionedUsers();
//                    for (User user : mentionedUsers)
//                    {
//                        Member member = guild.getMember(user);  //We get the member object for each mentioned user to kick them!
//
//                        //We need to make sure that we can interact with them. Interacting with a Member means you are higher
//                        // in the Role hierarchy than they are. Remember, NO ONE is above the Guild's Owner. (Guild#getOwner())
//                        if (!selfMember.canInteract(member))
//                        {
//                            // use the MessageAction to construct the content in StringBuilder syntax using append calls
//                            channel.sendMessage("Cannot kick member: ")
//                                   .append(member.getEffectiveName())
//                                   .append(", they are higher in the hierarchy than I am!")
//                                   .queue();
//                            continue;   //Continue to the next mentioned user to be kicked.
//                        }
//
//                        //Remember, due to the fact that we're using queue we will never have to deal with RateLimits.
//                        // JDA will do it all for you so long as you are using queue!
//                        guild.getController().kick(member).queue(
//                            success -> channel.sendMessage("Kicked ").append(member.getEffectiveName()).append("! Cya!").queue(),
//                            error ->
//                            {
//                                //The failure consumer provides a throwable. In this case we want to check for a PermissionException.
//                                if (error instanceof PermissionException)
//                                {
//                                    PermissionException pe = (PermissionException) error;
//                                    Permission missingPermission = pe.getPermission();  //If you want to know exactly what permission is missing, this is how.
//                                                                                        //Note: some PermissionExceptions have no permission provided, only an error message!
//
//                                    channel.sendMessage("PermissionError kicking [")
//                                           .append(member.getEffectiveName()).append("]: ")
//                                           .append(error.getMessage()).queue();
//                                }
//                                else
//                                {
//                                    channel.sendMessage("Unknown error while kicking [")
//                                           .append(member.getEffectiveName())
//                                           .append("]: <").append(error.getClass().getSimpleName()).append(">: ")
//                                           .append(error.getMessage()).queue();
//                                }
//                            });
//                    }
//                }
//            }
//            else
//            {
//                channel.sendMessage("This is a Guild-Only command!").queue();
//            }
//        }
//        else if (msg.equals("!block"))
//        {
//            //This is an example of how to use the complete() method on RestAction. The complete method acts similarly to how
//            // JDABuilder's buildBlocking works, it waits until the request has been sent before continuing execution.
//            //Most developers probably wont need this and can just use queue. If you use complete, JDA will still handle ratelimit
//            // control, however if shouldQueue is false it won't queue the Request to be sent after the ratelimit retry after time is past. It
//            // will instead fire a RateLimitException!
//            //One of the major advantages of complete() is that it returns the object that queue's success consumer would have,
//            // but it does it in the same execution context as when the request was made. This may be important for most developers,
//            // but, honestly, queue is most likely what developers will want to use as it is faster.
//
//            try
//            {
//                //Note the fact that complete returns the Message object!
//                //The complete() overload queues the Message for execution and will return when the message was sent
//                //It does handle rate limits automatically
//                Message sentMessage = channel.sendMessage("I blocked and will return the message!").complete();
//                //This should only be used if you are expecting to handle rate limits yourself
//                //The completion will not succeed if a rate limit is breached and throw a RateLimitException
//                Message sentRatelimitMessage = channel.sendMessage("I expect rate limitation and know how to handle it!").complete(false);
//
//                System.out.println("Sent a message using blocking! Luckly I didn't get Ratelimited... MessageId: " + sentMessage.getId());
//            }
//            catch (RateLimitedException e)
//            {
//                System.out.println("Whoops! Got ratelimited when attempting to use a .complete() on a RestAction! RetryAfter: " + e.getRetryAfter());
//            }
//            //Note that RateLimitException is the only checked-exception thrown by .complete()
//            catch (RuntimeException e)
//            {
//                System.out.println("Unfortunately something went wrong when we tried to send the Message and .complete() threw an Exception.");
//                e.printStackTrace();
//            }
//        }
    }
}
