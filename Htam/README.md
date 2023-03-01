# This is a guide on how to use Htam.

## A symbol-based, mostly-spaceless language.

This language was developed by Khushi Sharma in the spring of 2022 during Honors Programming Languages at The Westminster Schools.

## Data types:

**When initializing a new variable, it's integral to define its type. There are 3 possible options a type could qualify as.**

- `#` = Number (technically a double in Java)
- `§` = String
- `ß` = Boolean (`τ` = true , `Π` = false)

## Variables:

### New variable initialization:

**Again, there are 3 possible types a variable can qualify as, and you specify these variable types during initialization. Please note that this language doesn't support empty variable declarations, meaning that every variable must be assigned a value from its declaration.**

```

ν#num•1.0
ν§str•"Hello!"
νßbool•τ

```

## Operators:

### Assignment Operators:

| Syntax      | Description
| :---:        |    :----:   
| `Σ+`        | Addition
| `Σ-`        | Subtraction
| `μ`         | Multiplication  
| `δ`         | Division
| `%`         | Modulus

#### All assignment operators are used in a pre-fixed manner, meaning that the values being added occur after the operation symbol.
##### See below:

`
Σ+23•5
`

### Comparison Operators:

| Syntax      | Description
| :---:        |    :----:   
| `•`         | =
| `••`        | == (Is equal to?)
| `…`         | != (Not equal to)
| `ο`         | >  
| `˙`         | <
| `ο•`        | ≥
| `˙•`        | ≤

#### On the other hand, all comparison operators are used in an in-fixed manner, meaning that the values being added flank either side of the operation.
##### See below:

`
Σ+23••5
Output: τ
`

#### The rules for logical operators are the same as those for comparison operators.
##### See below:

### Logical Operators:

| Syntax      | Description
| :---:       |    :----:   
| `α`         | AND
| `Ω`         | OR
| `β`         | ! (NOT)

#### The rules for logical operators are the same as those for comparison operators.
##### See below:

`
Σ+23••5αΣ-32••1
Output: τ
`

## Control Flow:

### Conditions:

#### If Statements:

```
ι()«insert code block here»
```

#### If-Else Statements:

```
ιε()«insert code block here»
```

#### Else Statements:

```
ε«insert code block here»
```

##### Note that only the if statement can stand alone, just as in Java.
##### Additionally, conditions can be defined in a manner similar to as in Java, utilising all types of operators to do so.

### Loops:

#### While Statements:

```
ς()«insert code block here»
```

##### Like with if-else loops, while loop conditions can be defined using all types of operators.

#### For Statements:

```
φ(statement1,statement2,statement3)«insert code block here»
```

## Arrays:

**The elements of a single array can vary in type; furthermore, the type of an array doesn't need to be declared at initialization.**

### Initialization:

```
ּνυarr•{"Hello",1,2.0,τ}
```

### Element Access/Alteration:

```
ּarr[1]
arr[1]•1
```
### Length:

```
arr.ℓ
Output: 4
```

### Multi-Dimensional Arrays

```
νυ²arr•{"Hello",1,2.0,τ}{0,1,2,3}
```

## Methods

### Initialization:

```
νχmet(insert args here)«insert code block here»

```

**While there is no defined return type for methods, argument types must be defined with an identifier to be used within the block. See the example below:**

```
νχmet(#x)«insert code block here»

```

### Calls:

**When calling a function, make sure to specify all arguments in the parenthetical body!**

```
χmet(1.0, "Hello")
```

### Returns:

**You're allowed to return anything that's syntactically correct!**

```
ρ(1.0)
```

### Recursion:

**Recursion is performed by simply calling a function within a function.**

```
χmet(insert args here)«
    χmet(insert args here)«
      »
  »
```

## Built-In Functions:

### Printing:

```
π("Hello!")
Output: Hello!
```

**And that's all! If you have any further questions or suggestions, please feel free to reach out!**
