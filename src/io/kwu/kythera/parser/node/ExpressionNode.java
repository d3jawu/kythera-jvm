package io.kwu.kythera.parser.node;

/**
 * ExpressionNodes are statements that evaluate to a value and therefore also have a type.
 */
public abstract class ExpressionNode extends StatementNode {
    public ExpressionNode typeExp; // expression that actually gave this value its type

    ExpressionNode(NodeKind kind) {
        super(kind);
    }

    ExpressionNode(NodeKind kind, ExpressionNode typeExp) {
        super(kind);
        this.typeExp = typeExp;
    }

    /*

    There are two different operations I'm describing here:
    "as type" and "type of", which don't seem to be the same

    A type constraint becomes a value constraint on a type value...

    Essentially, every value is two values: the type value and the content value.

    There are two kinds of types: the types that are manipulated at runtime and static-analysis types the parser is aware of.

    We need something that can generate type literals from the static type constraints of nodes
    A sort of compile-time "typeof"

    Also, some types don't come from expressions, they just have types to begin with

    One way or another, we need to be able to go to and from types <-> expressions

    In the JS implementation, "type" ParseNodes were used as both syntax nodes and type nodes.
    We need to use TypeLiteralNodes as our compile-time type values as well.

    But then we'd have to use TypeLiteralNodes to describe TypeLiteralNodes... oh noooo

    Eventually all type expressions should boil down to a TypeLiteralNode, whether explicit or created by the compiler.
    The TypeLiteralNodes themselves have type "type".

    typeof(int) ==> <number> // but you can't return a function call...
    typeof(int) ==> number // how will the compiler know that int is a subtype of scalar and not some other supertype that int fulfills?
    typeof(int) ==> type // works fine for now, but falls apart when we need to be able to expect certain members

    we could allow:
    type {
        ... members go here, <: denotes subtype, >: denotes supertype
    }

    yeah, let's do it like this:

    typeof(int) ==> {
        [function type describing add op] add
        [function type describing subtract op] subtract
        etc
    }

    then, we expose a type "number" as a variable that = {
        [function type describing add op] add
        [function type describing subtract op] subtract
        etc
    }

    that int just happens to fulfill.

    which means that when we instantiate the int type literal

    abstract types are actually type /constructors/:
    int = number({...})
    the number() function takes some implementation for int
    so how do we get the "number" part back? we have to retain a reference to the type constructor that created this type
    that way we can know what type it's a subtype of!

     */
}
