import csv

students = {}
FILE = "students.csv"


class Student:
    def __init__(self, name, age, favorites):
        self.name = name
        self.age = age
        self.favorites = favorites

    def __str__(self):
        return f"""
Student {{
    name: \"{self.name}\"
    age: {self.age}
    favorites: {self.favorites}
}}"""


def get_student() -> Student:
    name = input("Name: ")
    age = input("Age: ")
    while True:
        try:
            age = int(age)
        except ValueError:
            print(f"Invalid number: {age}")
            age = input("Age: ")
            continue
        break
    favorites = input("Their favorite subjects: ")
    favorites = list(map(str.strip, favorites.split(",")))
    return Student(name, age, favorites)


def print_table(table, max_age=100):
    table = list(filter(lambda student: student.age < max_age, table.values()))
    if len(table) == 0:
        print("Empty.")
        return
    PADDING = 1

    name_column_width = max([len(student.name) for student in table]) + PADDING * 2
    age_column_width = max([len(str(student.age)) for student in table]) + PADDING * 2
    favorites_column_width = max([len(" ".join(student.favorites)) for student in table]) + PADDING * 2

    PADDING = " " * PADDING
    padded = lambda item: f"{PADDING}{item}{PADDING}"

    def adjusted_width(width, current):
        return max([width, current])

    name_column_width = adjusted_width(len(padded('name')), name_column_width)
    age_column_width = adjusted_width(len(padded('age')), age_column_width)
    favorites_column_width = adjusted_width(len(padded('favorite subjects')), favorites_column_width)

    name = padded('name').ljust(name_column_width)
    age = padded('age').ljust(age_column_width)
    favorites = padded('favorites subjects').ljust(favorites_column_width)
    header = f"{name}|{age}|{favorites}"

    print(header)
    print("-" * len(header))
    for student in table:
        name = padded(student.name).ljust(name_column_width)
        age = padded(str(student.age)).ljust(age_column_width)
        favorites = padded(" ".join(student.favorites)).ljust(favorites_column_width)

        print(f"{name}|{age}|{favorites}")
    print()


def read_csv() -> list[Student]:
    with open(FILE, mode="r") as file:
        csv_file = csv.reader(file)
        for data in csv_file:
            student = Student(data[0], int(data[1]), data[2:])
            print(f"Imported student: {student}")
            students[student.name] = student
        return []


def dump_csv(students):
    with open(FILE, mode="w") as file:
        for student in students.values():
            file.write(f"{student.name},{student.age},{','.join(student.favorites)}\n")


def main():
    read_csv()
    while True:
        student: Student = get_student()
        print()
        if student.name in students:
            print(f"{student.name} already exists - overwiting.")
        students[student.name] = student
        value = input("Input \"stop\" to stop or anything else add another student: ")
        if value == "stop":
            break

    print_table(students)

    dump_csv(students)


if __name__ == "__main__":
    main()
